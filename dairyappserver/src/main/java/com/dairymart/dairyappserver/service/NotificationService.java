package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.AppNotificationDao;
import com.dairymart.dairyappserver.dao.RetailOrderDao;
import com.dairymart.dairyappserver.dao.ScheduledNotificationDao;
import com.dairymart.dairyappserver.dao.ShopDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.AppNotificationDTO;
import com.dairymart.dairyappserver.dto.ScheduledNotificationDTO;
import com.dairymart.dairyappserver.repository.AppNotificationRepository;
import com.dairymart.dairyappserver.repository.ScheduledNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private AppNotificationRepository notificationRepository;

    @Autowired
    private ScheduledNotificationRepository scheduledNotificationRepository;

    private static final int ACTIVITY_FEED_USER_ID = -1;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private SalesmanToRetailService salesmanToRetailService;

    public List<AppNotificationDao> getForUser(int userId) {
        return notificationRepository.findAll().stream()
                .filter(n -> n.getUserId() == userId)
                .sorted((a, b) -> {
                    if (a.getCreatedOn() == null || b.getCreatedOn() == null) {
                        return Long.compare(b.getNotificationId(), a.getNotificationId());
                    }
                    return b.getCreatedOn().compareTo(a.getCreatedOn());
                })
                .limit(100)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public long unreadCount(int userId) {
        return getForUser(userId).stream().filter(n -> !n.isRead()).count();
    }

    public AppNotificationDao markRead(long notificationId) {
        AppNotificationDao dao = notificationRepository.findById(notificationId).orElse(null);
        if (dao == null) {
            return null;
        }
        dao.setRead(true);
        return notificationRepository.save(dao);
    }

    public Map<String, Object> sendOrSchedule(AppNotificationDTO dto) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("title", dto == null ? "" : dto.getTitle());
        if (dto != null && dto.getScheduledForMillis() > System.currentTimeMillis() + 5000) {
            ScheduledNotificationDTO saved = schedule(dto);
            json.put("scheduled", true);
            json.put("sent", 0);
            json.put("scheduleId", saved.getScheduleId());
            json.put("scheduledForMillis", saved.getScheduledForMillis());
            return json;
        }
        json.put("scheduled", false);
        json.put("sent", broadcast(dto));
        return json;
    }

    public ScheduledNotificationDTO schedule(AppNotificationDTO dto) {
        if (dto == null || dto.getTitle() == null || dto.getTitle().isBlank()
                || dto.getMessage() == null || dto.getMessage().isBlank()) {
            throw new IllegalArgumentException("Title and message are required.");
        }
        if (dto.getScheduledForMillis() <= System.currentTimeMillis()) {
            throw new IllegalArgumentException("Pick a future date and time for this notice.");
        }
        ScheduledNotificationDao row = new ScheduledNotificationDao();
        row.setTitle(dto.getTitle().trim());
        row.setMessage(dto.getMessage().trim());
        row.setKind(dto.getKind() == null || dto.getKind().isBlank() ? "ANNOUNCEMENT" : dto.getKind().trim().toUpperCase());
        row.setAudience(dto.getAudience() == null || dto.getAudience().isBlank() ? "ALL" : dto.getAudience().trim().toUpperCase());
        row.setScheduledFor(new Timestamp(dto.getScheduledForMillis()));
        row.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        row.setReleased(false);
        row.setCancelled(false);
        ScheduledNotificationDao saved = scheduledNotificationRepository.save(row);
        recordActivity("ANNOUNCEMENT", "Notice scheduled",
                saved.getTitle() + " at " + saved.getScheduledFor(), 0, saved.getScheduleId());
        return new ScheduledNotificationDTO(saved);
    }

    public List<ScheduledNotificationDTO> listScheduled() {
        List<ScheduledNotificationDTO> rows = new ArrayList<>();
        for (ScheduledNotificationDao dao : scheduledNotificationRepository.findAll()) {
            if (dao.isCancelled()) {
                continue;
            }
            rows.add(new ScheduledNotificationDTO(dao));
        }
        rows.sort((a, b) -> Long.compare(a.getScheduledForMillis(), b.getScheduledForMillis()));
        return rows;
    }

    public ScheduledNotificationDTO cancelScheduled(long scheduleId) {
        ScheduledNotificationDao dao = scheduledNotificationRepository.findById(scheduleId).orElse(null);
        if (dao == null) {
            throw new IllegalArgumentException("That scheduled notice was not found.");
        }
        if (dao.isReleased()) {
            throw new IllegalArgumentException("That notice has already been sent.");
        }
        dao.setCancelled(true);
        return new ScheduledNotificationDTO(scheduledNotificationRepository.save(dao));
    }

    public int releaseDueScheduled() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        int released = 0;
        for (ScheduledNotificationDao dao : scheduledNotificationRepository.findAll()) {
            if (dao.isReleased() || dao.isCancelled() || dao.getScheduledFor() == null
                    || dao.getScheduledFor().after(now)) {
                continue;
            }
            AppNotificationDTO dto = new AppNotificationDTO();
            dto.setTitle(dao.getTitle());
            dto.setMessage(dao.getMessage());
            dto.setKind(dao.getKind());
            dto.setAudience(dao.getAudience());
            broadcast(dto);
            dao.setReleased(true);
            scheduledNotificationRepository.save(dao);
            released++;
        }
        return released;
    }

    public int broadcast(AppNotificationDTO dto) {
        if (dto == null || dto.getTitle() == null || dto.getTitle().isBlank()
                || dto.getMessage() == null || dto.getMessage().isBlank()) {
            throw new IllegalArgumentException("Title and message are required.");
        }
        String kind = dto.getKind() == null || dto.getKind().isBlank() ? "ANNOUNCEMENT" : dto.getKind().trim().toUpperCase();
        String audience = dto.getAudience() == null || dto.getAudience().isBlank() ? "ALL" : dto.getAudience().trim().toUpperCase();
        Set<Integer> recipients = new LinkedHashSet<>();
        List<UserDao> users;
        if ("SALESMEN".equals(audience)) {
            users = userService.findByTypeId(2);
        } else if ("RETAILERS".equals(audience)) {
            users = userService.findByTypeId(3);
        } else if ("ADMINS".equals(audience)) {
            users = userService.findByTypeId(1);
        } else {
            users = userService.getAllUsers();
            audience = "ALL";
        }
        for (UserDao user : users) {
            if (user != null && !Boolean.FALSE.equals(user.getActive())) {
                recipients.add(user.getUserId());
            }
        }
        addAdmins(recipients);
        push(recipients, kind, dto.getTitle().trim(), dto.getMessage().trim(), 0);
        recordActivity(kind, dto.getTitle().trim(),
                "Broadcast to " + audience + ": " + dto.getMessage().trim(), 0, recipients.size());
        return recipients.size();
    }

    public void notifyOrderCreated(RetailOrderDao order) {
        notifyOrder(order, "ORDER", "New order placed",
                "Order #" + order.getOrderId() + " was placed.", order.getOrderId());
    }

    public void notifyOrderStatusChanged(RetailOrderDao order, int previousStatusId, int newStatusId) {
        if (previousStatusId == newStatusId) {
            return;
        }
        notifyOrder(order, "ORDER", "Order status updated",
                "Order #" + order.getOrderId() + " is now " + statusLabel(newStatusId) + ".",
                order.getOrderId());
    }

    public void notifyLedgerTransaction(int salesmanId, int retailerUserId, boolean credit, double amount, long refId) {
        String title = credit ? "Payment collected" : "Ledger charge";
        String message = (credit ? "Collection of ₹" : "Charge of ₹")
                + String.format("%.2f", amount)
                + (credit ? " was recorded." : " was added to the ledger.");
        Set<Integer> recipients = new LinkedHashSet<>();
        if (salesmanId > 0) {
            recipients.add(salesmanId);
        }
        if (retailerUserId > 0) {
            recipients.add(retailerUserId);
        }
        addAdmins(recipients);
        push(recipients, "LEDGER", title, message, refId);
        recordActivity("LEDGER", title, message, salesmanId > 0 ? salesmanId : retailerUserId, refId);
    }

    private void notifyOrder(RetailOrderDao order, String kind, String title, String message, long refId) {
        Set<Integer> recipients = new LinkedHashSet<>();
        ShopDao shop = shopService.findById(order.getRetailerId());
        int retailerUserId = shop != null ? shop.getUserId() : order.getCreatedBy();
        if (retailerUserId > 0) {
            recipients.add(retailerUserId);
        }
        Integer salesmanId = resolveSalesman(order.getRetailerId(), retailerUserId);
        if (salesmanId != null) {
            recipients.add(salesmanId);
        }
        addAdmins(recipients);
        push(recipients, kind, title, message, refId);
        recordActivity(kind, title, message, retailerUserId, refId);
    }

    private Integer resolveSalesman(int shopId, int retailerUserId) {
        return salesmanToRetailService.getAllSalestoRetail().stream()
                .filter(m -> m.getRetailerId() == shopId || m.getRetailerId() == retailerUserId)
                .map(m -> m.getSalesmanId())
                .findFirst()
                .orElse(null);
    }

    private void addAdmins(Set<Integer> recipients) {
        for (UserDao admin : userService.findByTypeId(1)) {
            recipients.add(admin.getUserId());
        }
    }

    private void push(Set<Integer> userIds, String kind, String title, String message, long refId) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (Integer userId : userIds) {
            if (userId == null || userId == ACTIVITY_FEED_USER_ID) {
                continue;
            }
            AppNotificationDao dao = new AppNotificationDao();
            dao.setUserId(userId);
            dao.setKind(kind);
            dao.setTitle(clip(title));
            dao.setMessage(clip(message));
            dao.setRefId(refId);
            dao.setRead(false);
            dao.setCreatedOn(now);
            notificationRepository.save(dao);
        }
        logger.info("Notification [{}] sent to {} user(s): {}", kind, userIds.size(), title);
    }

    public void recordActivity(String kind, String title, String message, int actorUserId, long refId) {
        try {
            AppNotificationDao dao = new AppNotificationDao();
            dao.setUserId(ACTIVITY_FEED_USER_ID);
            dao.setKind(kind == null ? "ACTIVITY" : kind);
            dao.setTitle(clip(title));
            dao.setMessage(clip(message));
            dao.setRefId(refId);
            dao.setRead(true);
            dao.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            notificationRepository.save(dao);
        } catch (Exception ex) {
            logger.warn("Could not record activity: {}", ex.getMessage());
        }
    }

    public List<AppNotificationDTO> getPlatformActivity() {
        Map<String, AppNotificationDao> unique = new LinkedHashMap<>();
        List<AppNotificationDao> all = new ArrayList<>(notificationRepository.findAll());
        all.sort((a, b) -> {
            if (a.getCreatedOn() == null || b.getCreatedOn() == null) {
                return Long.compare(b.getNotificationId(), a.getNotificationId());
            }
            int byTime = b.getCreatedOn().compareTo(a.getCreatedOn());
            return byTime != 0 ? byTime : Long.compare(b.getNotificationId(), a.getNotificationId());
        });
        for (AppNotificationDao n : all) {
            String key = String.valueOf(n.getKind()) + '|' + n.getTitle() + '|' + n.getMessage() + '|'
                    + String.valueOf(n.getCreatedOn());
            unique.putIfAbsent(key, n);
        }
        return unique.values().stream().limit(200).map(AppNotificationDTO::new).collect(Collectors.toCollection(ArrayList::new));
    }

    public static String roleLabel(int typeId) {
        return switch (typeId) {
            case 1 -> "Admin";
            case 2 -> "Salesman";
            case 3 -> "Retailer";
            default -> "User";
        };
    }

    public static String personName(UserDao user) {
        if (user == null) {
            return "";
        }
        return ((user.getFirstName() == null ? "" : user.getFirstName()) + " "
                + (user.getLastName() == null ? "" : user.getLastName())).trim();
    }

    private String clip(String value) {
        if (value == null) {
            return "";
        }
        return value.length() <= 250 ? value : value.substring(0, 247) + "...";
    }

    private String statusLabel(int statusId) {
        return switch (statusId) {
            case 1 -> "NEW";
            case 2 -> "CONFIRMED";
            case 3 -> "REJECTED";
            case 4 -> "DISPATCHED";
            case 5 -> "DELIVERED";
            case 6 -> "RETURNED";
            case 7 -> "CANCELLED";
            default -> "status " + statusId;
        };
    }
}
