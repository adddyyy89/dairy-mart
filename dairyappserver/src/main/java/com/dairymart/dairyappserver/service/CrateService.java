package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.CrateDao;
import com.dairymart.dairyappserver.dao.CratePoolDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.CrateHolderDTO;
import com.dairymart.dairyappserver.dto.CrateMovementDTO;
import com.dairymart.dairyappserver.dto.CrateSummaryDTO;
import com.dairymart.dairyappserver.repository.CratePoolRepository;
import com.dairymart.dairyappserver.repository.CrateRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrateService {

    private static final int POOL_ID = 1;
    Logger logger = LoggerFactory.getLogger(CrateService.class);

    @Autowired
    private CrateRepository crateRepository;

    @Autowired
    private CratePoolRepository cratePoolRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SalesmanToRetailService salesmanToRetailService;

    @Autowired
    private ShopService shopService;

    @Autowired
    @Lazy
    private NotificationService notificationService;

    public CrateDao getLatest(int userId) {
        return crateRepository.findAll().stream()
                .filter(c -> c.getUserId() == userId)
                .max(Comparator.comparing(CrateDao::getRecordTimestamp, Comparator.nullsFirst(Timestamp::compareTo)))
                .orElse(null);
    }

    public CrateDao updateCrate(CrateDao dao) {
        UserDao user = userService.findById(dao.getUserId());
        if (user == null) {
            logger.error("User id does not exist in system!!");
            return null;
        }
        if (user.getTypeId() == 3) {
            throw new IllegalArgumentException("Stores cannot edit crate data.");
        }
        if (dao.getUserTypeId() <= 0) {
            dao.setUserTypeId(user.getTypeId());
        }
        return saveSnapshot(user, dao.getCrateCount(), dao.getCrateReceived(), dao.getCrateReturned());
    }

    public List<CrateDao> getTotalCrates() {
        List<CrateDao> crateDaos = crateRepository.findAll();
        crateDaos.sort((o1, o2) -> {
            if (o1.getRecordTimestamp() == null || o2.getRecordTimestamp() == null) {
                return 0;
            }
            return o1.getRecordTimestamp().compareTo(o2.getRecordTimestamp());
        });
        return crateDaos;
    }

    public List<CrateDao> getTotalCratesForUser(int userId) {
        return crateRepository.findAll().stream()
                .filter(c -> c.getUserId() == userId)
                .sorted(Comparator.comparing(CrateDao::getRecordTimestamp, Comparator.nullsFirst(Timestamp::compareTo)).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Integer getCurrentAssignedCrateForUser(int userId) {
        CrateDao dao = getLatest(userId);
        if (dao != null) {
            UserDao user = userService.findById(userId);
            if (user != null && user.getTypeId() == 3) {
                return Math.max(0, dao.getCrateReceived() - dao.getCrateReturned());
            }
            return Math.max(0, dao.getCrateCount());
        }
        UserDao user = userService.findById(userId);
        return user != null ? user.getCrateCount() : 0;
    }

    public CratePoolDao getPool() {
        return cratePoolRepository.findById(POOL_ID).orElseGet(() -> {
            CratePoolDao pool = new CratePoolDao();
            pool.setPoolId(POOL_ID);
            pool.setTotalCrates(0);
            pool.setAvailableAtBranch(0);
            pool.setLastUpdated(now());
            return cratePoolRepository.save(pool);
        });
    }

    @Transactional
    public CratePoolDao addToPool(int quantity) {
        requirePositive(quantity);
        CratePoolDao pool = getPool();
        pool.setTotalCrates(pool.getTotalCrates() + quantity);
        pool.setAvailableAtBranch(pool.getAvailableAtBranch() + quantity);
        pool.setLastUpdated(now());
        logger.info("Added {} crates to branch pool. Total={}, available={}",
                quantity, pool.getTotalCrates(), pool.getAvailableAtBranch());
        CratePoolDao saved = cratePoolRepository.save(pool);
        if (notificationService != null) {
            notificationService.recordActivity("CRATE", "Crates added",
                    quantity + " crates added to the branch pool.", 0, quantity);
        }
        return saved;
    }

    @Transactional
    public CrateDao assignToSalesman(int salesmanId, int quantity) {
        requirePositive(quantity);
        UserDao salesman = requireRole(salesmanId, 2, "salesman");
        CratePoolDao pool = getPool();
        if (pool.getAvailableAtBranch() < quantity) {
            throw new IllegalArgumentException("Not enough crates at the branch. Available: " + pool.getAvailableAtBranch());
        }
        CrateDao latest = snapshotOrEmpty(salesman);
        pool.setAvailableAtBranch(pool.getAvailableAtBranch() - quantity);
        pool.setLastUpdated(now());
        cratePoolRepository.save(pool);
        CrateDao saved = saveSnapshot(salesman, latest.getCrateCount() + quantity,
                latest.getCrateReceived(), latest.getCrateReturned());
        logger.info("Assigned {} crates to salesman {}", quantity, salesmanId);
        if (notificationService != null) {
            notificationService.recordActivity("CRATE", "Crates assigned",
                    quantity + " crates assigned to " + NotificationService.personName(salesman) + ".",
                    salesmanId, salesmanId);
        }
        return saved;
    }

    @Transactional
    public CrateDao sendToStore(CrateMovementDTO dto) {
        requirePositive(dto.getQuantity());
        UserDao salesman = requireRole(dto.getSalesmanId(), 2, "salesman");
        UserDao store = requireStoreForSalesman(dto.getSalesmanId(), dto.getRetailerUserId());
        CrateDao salesmanSnap = snapshotOrEmpty(salesman);
        if (salesmanSnap.getCrateCount() < dto.getQuantity()) {
            throw new IllegalArgumentException("Salesman does not hold enough crates. Holding: " + salesmanSnap.getCrateCount());
        }
        CrateDao storeSnap = snapshotOrEmpty(store);
        saveSnapshot(salesman,
                salesmanSnap.getCrateCount() - dto.getQuantity(),
                salesmanSnap.getCrateReceived() + dto.getQuantity(),
                salesmanSnap.getCrateReturned());
        CrateDao saved = saveSnapshot(store,
                storeSnap.getCrateCount() + dto.getQuantity(),
                storeSnap.getCrateReceived() + dto.getQuantity(),
                storeSnap.getCrateReturned());
        logger.info("Salesman {} sent {} crates to store {}", dto.getSalesmanId(), dto.getQuantity(), store.getUserId());
        if (notificationService != null) {
            notificationService.recordActivity("CRATE", "Crates sent to store",
                    dto.getQuantity() + " crates sent to " + NotificationService.personName(store) + ".",
                    dto.getSalesmanId(), store.getUserId());
        }
        return saved;
    }

    @Transactional
    public CrateDao returnFromStore(CrateMovementDTO dto) {
        requirePositive(dto.getQuantity());
        UserDao salesman = requireRole(dto.getSalesmanId(), 2, "salesman");
        UserDao store = requireStoreForSalesman(dto.getSalesmanId(), dto.getRetailerUserId());
        CrateDao storeSnap = snapshotOrEmpty(store);
        int atStore = Math.max(0, storeSnap.getCrateReceived() - storeSnap.getCrateReturned());
        if (atStore < dto.getQuantity()) {
            throw new IllegalArgumentException("Store does not have that many crates. At store: " + atStore);
        }
        CrateDao salesmanSnap = snapshotOrEmpty(salesman);
        saveSnapshot(store,
                storeSnap.getCrateCount(),
                storeSnap.getCrateReceived(),
                storeSnap.getCrateReturned() + dto.getQuantity());
        CrateDao saved = saveSnapshot(salesman,
                salesmanSnap.getCrateCount() + dto.getQuantity(),
                Math.max(0, salesmanSnap.getCrateReceived() - dto.getQuantity()),
                salesmanSnap.getCrateReturned());
        logger.info("Salesman {} collected {} crates from store {}", dto.getSalesmanId(), dto.getQuantity(), store.getUserId());
        if (notificationService != null) {
            notificationService.recordActivity("CRATE", "Crates collected from store",
                    dto.getQuantity() + " crates collected from " + NotificationService.personName(store) + ".",
                    dto.getSalesmanId(), store.getUserId());
        }
        return saved;
    }

    @Transactional
    public CrateDao returnToBranch(int salesmanId, int quantity) {
        requirePositive(quantity);
        UserDao salesman = requireRole(salesmanId, 2, "salesman");
        CrateDao latest = snapshotOrEmpty(salesman);
        if (latest.getCrateCount() < quantity) {
            throw new IllegalArgumentException("Salesman does not hold enough crates to return. Holding: " + latest.getCrateCount());
        }
        CratePoolDao pool = getPool();
        pool.setAvailableAtBranch(pool.getAvailableAtBranch() + quantity);
        pool.setLastUpdated(now());
        cratePoolRepository.save(pool);
        CrateDao saved = saveSnapshot(salesman,
                latest.getCrateCount() - quantity,
                latest.getCrateReceived(),
                latest.getCrateReturned() + quantity);
        logger.info("Salesman {} returned {} crates to branch", salesmanId, quantity);
        if (notificationService != null) {
            notificationService.recordActivity("CRATE", "Crates returned to branch",
                    quantity + " crates returned to branch by " + NotificationService.personName(salesman) + ".",
                    salesmanId, salesmanId);
        }
        return saved;
    }

    public CrateSummaryDTO getSummary() {
        CratePoolDao pool = getPool();
        CrateSummaryDTO summary = new CrateSummaryDTO();
        summary.setAtBranch(pool.getAvailableAtBranch());
        int withSalesmen = 0;
        int atStores = 0;
        List<CrateHolderDTO> salesmanRows = new ArrayList<>();
        List<CrateHolderDTO> storeRows = new ArrayList<>();
        for (UserDao salesman : userService.findByTypeId(2)) {
            CrateDao snap = snapshotOrEmpty(salesman);
            CrateHolderDTO row = holderFrom(salesman, snap);
            row.setHolding(snap.getCrateCount());
            row.setSentToStores(snap.getCrateReceived());
            row.setReturnedToBranch(snap.getCrateReturned());
            withSalesmen += snap.getCrateCount();
            salesmanRows.add(row);
        }
        for (UserDao store : userService.findByTypeId(3)) {
            CrateDao snap = snapshotOrEmpty(store);
            int atStore = Math.max(0, snap.getCrateReceived() - snap.getCrateReturned());
            if (snap.getCrateCount() == 0 && atStore == 0 && snap.getCrateReturned() == 0) {
                continue;
            }
            CrateHolderDTO row = holderFrom(store, snap);
            var shops = shopService.getShopByRetailerId(store.getUserId());
            if (!shops.isEmpty() && shops.get(0).getShopName() != null && !shops.get(0).getShopName().isBlank()) {
                row.setName(shops.get(0).getShopName());
            }
            row.setSentToStores(snap.getCrateCount());
            row.setAtStore(atStore);
            row.setReturnedFromStore(snap.getCrateReturned());
            atStores += atStore;
            storeRows.add(row);
        }
        summary.setWithSalesmen(withSalesmen);
        summary.setAtStores(atStores);
        summary.setTotalInSystem(pool.getAvailableAtBranch() + withSalesmen + atStores);
        summary.setSalesmen(salesmanRows);
        summary.setStores(storeRows);
        return summary;
    }

    private CrateDao saveSnapshot(UserDao user, int crateCount, int crateReceived, int crateReturned) {
        CrateDao dao = new CrateDao();
        dao.setUserId(user.getUserId());
        dao.setUserTypeId(user.getTypeId());
        dao.setCrateCount(Math.max(0, crateCount));
        dao.setCrateReceived(Math.max(0, crateReceived));
        dao.setCrateReturned(Math.max(0, crateReturned));
        dao.setRecordTimestamp(now());
        int holding = user.getTypeId() == 3
                ? Math.max(0, dao.getCrateReceived() - dao.getCrateReturned())
                : dao.getCrateCount();
        user.setCrateCount(holding);
        userService.saveUser(user);
        return crateRepository.save(dao);
    }

    private CrateDao snapshotOrEmpty(UserDao user) {
        CrateDao latest = getLatest(user.getUserId());
        if (latest != null) {
            return latest;
        }
        CrateDao empty = new CrateDao();
        empty.setUserId(user.getUserId());
        empty.setUserTypeId(user.getTypeId());
        empty.setCrateCount(Math.max(0, user.getCrateCount()));
        return empty;
    }

    private UserDao requireRole(int userId, int typeId, String label) {
        UserDao user = userService.findById(userId);
        if (user == null || user.getTypeId() != typeId) {
            throw new IllegalArgumentException("Invalid " + label + " id.");
        }
        return user;
    }

    private UserDao requireStoreForSalesman(int salesmanId, int retailerUserId) {
        UserDao store = userService.findById(retailerUserId);
        if (store == null || store.getTypeId() != 3) {
            var shop = shopService.findById(retailerUserId);
            if (shop != null) {
                store = userService.findById(shop.getUserId());
            }
        }
        if (store == null || store.getTypeId() != 3) {
            throw new IllegalArgumentException("Invalid store/retailer user id.");
        }
        final int storeUserId = store.getUserId();
        boolean mapped = salesmanToRetailService.getAllRetailsforSalesman(salesmanId).stream()
                .map(m -> shopService.findById(m.getRetailerId()))
                .filter(shop -> shop != null)
                .anyMatch(shop -> shop.getUserId() == storeUserId || shop.getShopId() == retailerUserId);
        if (!mapped) {
            throw new IllegalArgumentException("That store is not assigned to this salesman.");
        }
        return store;
    }

    private CrateHolderDTO holderFrom(UserDao user, CrateDao snap) {
        CrateHolderDTO row = new CrateHolderDTO();
        row.setUserId(user.getUserId());
        row.setUserTypeId(user.getTypeId());
        row.setName(((user.getFirstName() == null ? "" : user.getFirstName()) + " "
                + (user.getLastName() == null ? "" : user.getLastName())).trim());
        row.setPhoneNumber(user.getPhoneNumber());
        return row;
    }

    private void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }
}
