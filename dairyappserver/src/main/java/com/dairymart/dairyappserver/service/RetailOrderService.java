package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.*;
import com.dairymart.dairyappserver.dto.RetailOrderDTO;
import com.dairymart.dairyappserver.dto.RetailOrderDetailsDTO;
import com.dairymart.dairyappserver.repository.OrderStatusRepository;
import com.dairymart.dairyappserver.repository.RetailOrderDetailsRepository;
import com.dairymart.dairyappserver.repository.RetailOrderRepository;
import com.dairymart.dairyappserver.util.DateUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RetailOrderService {

    Logger logger = LoggerFactory.getLogger(RetailOrderService.class);

    @Autowired
    private RetailOrderRepository retailOrderRepository;

    @Autowired
    private RetailOrderDetailsRepository retailOrderDetailsRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private SalesmanToRetailService salesmanToRetailService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BranchInventoryService branchInventoryService;

    public List<RetailOrderDao> getAllOrders() {
        return retailOrderRepository.findAll();
    }

    public RetailOrderDao createOrder(RetailOrderDTO order) {

        if (order.getOrderStatusId() <= 0) {
            order.setOrderStatusId(1);
        }
        if (order.getBranchId() <= 0) {
            order.setBranchId(7);
        }

        RetailOrderDao orderDao = new RetailOrderDao(order);
        orderDao.setCreatedon(new Date(System.currentTimeMillis()));
        orderDao.setLastUpdated(new Date(System.currentTimeMillis()));
        orderDao.setOrderDate(new Date(System.currentTimeMillis()));

        // Save Initial Order
        RetailOrderDao savedOrderDao = retailOrderRepository.save(orderDao);
        int lineCount = 0;
        if (order.getOrderDetails() != null) {
            for (RetailOrderDetailsDTO dto : order.getOrderDetails()) {
                if (dto == null || dto.getProductCode() == null || dto.getProductCode().isBlank()) {
                    continue;
                }
                dto.setLastUpdated(new Date(System.currentTimeMillis()));
                dto.setOrderId(savedOrderDao.getOrderId());
                retailOrderDetailsRepository.save(new RetailOrderDetailsDao(dto));
                lineCount++;
            }
        }
        logger.info("Created order {} with {} line(s)", savedOrderDao.getOrderId(), lineCount);
        RetailOrderDao created = findById(savedOrderDao.getOrderId());
        try {
            notificationService.notifyOrderCreated(created);
        } catch (Exception ex) {
            logger.error("Failed to send new-order notification for order {}", savedOrderDao.getOrderId(), ex);
        }
        return created;
    }

    @Transactional
    public RetailOrderDao updateOrder(RetailOrderDTO order) {

        Optional<RetailOrderDao> actualRetailOrderOpt = retailOrderRepository.findById(order.getOrderId());
        RetailOrderDao orderDao = actualRetailOrderOpt.get();
        int previousStatusId = orderDao.getOrderStatusId();

        orderDao.setLastUpdated(new Date(System.currentTimeMillis()));
        orderDao.setOrderStatusId(order.getOrderStatusId());
        branchInventoryService.applyStatusChange(orderDao, previousStatusId, order.getOrderStatusId());


        // Save Initial Order
        RetailOrderDao savedOrderDao = retailOrderRepository.save(orderDao);
        if(order.getOrderDetails() != null) {
            for(RetailOrderDetailsDTO dto : order.getOrderDetails()) {
                dto.setLastUpdated(new Date(System.currentTimeMillis()));
                retailOrderDetailsRepository.save(new RetailOrderDetailsDao(dto));
            }
        }
        logger.info("Updated order {} status {} -> {}", savedOrderDao.getOrderId(), previousStatusId, order.getOrderStatusId());
        RetailOrderDao updated = findById(savedOrderDao.getOrderId());
        maybePostOrderLedger(previousStatusId, order.getOrderStatusId(), updated);
        notifyStatusIfChanged(updated, previousStatusId, order.getOrderStatusId());
        return updated;
    }

    public RetailOrderDao findById(int id) {
        Optional<RetailOrderDao> dao = retailOrderRepository.findById(id);
        return dao.orElse(null);
    }

    public List<OrderStatusDao> getAllOrderStatus() {
        return orderStatusRepository.findAll();
    }


    @Transactional
    public RetailOrderDao updateOrderStatus(RetailOrderDTO retailOrderDTO) {
        Optional<RetailOrderDao> dao = retailOrderRepository.findById(retailOrderDTO.getOrderId());
        RetailOrderDao d = null;
        if(dao.isPresent()) {
            RetailOrderDao retailOrderDao = dao.get();
            int previousStatusId = retailOrderDao.getOrderStatusId();
            retailOrderDao.setLastUpdated(new Date(System.currentTimeMillis()));
            retailOrderDao.setOrderStatusId(retailOrderDTO.getOrderStatusId());
            branchInventoryService.applyStatusChange(retailOrderDao, previousStatusId, retailOrderDTO.getOrderStatusId());
            d = retailOrderRepository.save(retailOrderDao);
            RetailOrderDao updated = findById(d.getOrderId());
            maybePostOrderLedger(previousStatusId, retailOrderDTO.getOrderStatusId(), updated);
            notifyStatusIfChanged(updated, previousStatusId, retailOrderDTO.getOrderStatusId());
            return updated;
        }
        return d;
    }

    private void notifyStatusIfChanged(RetailOrderDao order, int previousStatusId, int newStatusId) {
        try {
            notificationService.notifyOrderStatusChanged(order, previousStatusId, newStatusId);
        } catch (Exception ex) {
            logger.error("Failed to send status notification for order {}", order != null ? order.getOrderId() : 0, ex);
        }
    }

    private void maybePostOrderLedger(int previousStatusId, int newStatusId, RetailOrderDao order) {
        if (order == null) {
            return;
        }
        if (previousStatusId == 1 && isBillableStatus(newStatusId)) {
            ledgerService.postOrderCharge(order);
        }
    }

    /** NEW=1 is not billed. Rejected/returned/cancelled do not create a receivable. */
    private boolean isBillableStatus(int statusId) {
        return statusId == 2 || statusId == 4 || statusId == 5;
    }

    public List<RetailOrderDao> getOrdersForRetailers(List<Integer> retailerIds) {
        List<RetailOrderDao> retailOrderDaos = new ArrayList<>();
        List<RetailOrderDao> orders = retailOrderRepository.findAll();
        for(Integer retailerId : retailerIds) {
            List<RetailOrderDao> orderDaos = orders.stream().filter(x->x.getRetailerId() == retailerId).collect(Collectors.toList());
            for(RetailOrderDao d : orderDaos) {
                retailOrderDaos.add(d);
            }
        }
        return retailOrderDaos;
    }

    /** Orders for shops assigned to this salesman ({@code salesmantoretail.retailerid} = shop id). */
    public List<RetailOrderDao> getOrdersForSalesman(int salesmanUserId) {
        List<Integer> shopIds = salesmanToRetailService.getAllRetailsforSalesman(salesmanUserId).stream()
                .map(SalesmanToRetailDao::getRetailerId)
                .collect(Collectors.toList());
        return getOrdersForRetailers(shopIds);
    }

    public UserDao getSalesmanUsingOrderId(int orderId) {
        List<RetailOrderDao> retailOrderDaos = retailOrderRepository.findAll().stream().filter(retailOrderDao -> retailOrderDao.getOrderId() == orderId).collect(Collectors.toCollection(ArrayList::new));
        int retailerId = retailOrderDaos.get(0).getRetailerId();
        return salesmanToRetailService.getSalesmanForRetailer(retailerId);
    }

    /**
     * Dashboard "orders placed" is keyed by user id, but {@code retail_order.retailer_id}
     * is the shop id (same as create / GET /retailorder/get/retailer/{userId}).
     */
    public List<RetailOrderDao> getCurrentOrdersPlaced(int retailerUserId) {
        List<Integer> shopIds = shopService.getShopByRetailerId(retailerUserId).stream()
                .map(ShopDao::getShopId)
                .collect(Collectors.toList());
        return retailOrderRepository.findAll().stream()
                .filter(x -> {
                    int rid = x.getRetailerId();
                    if (rid != retailerUserId && !shopIds.contains(rid)) {
                        return false;
                    }
                    if (x.getCreatedon() == null) {
                        return true;
                    }
                    return DateUtil.isSameDay(new Timestamp(x.getCreatedon().getTime()));
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /*public RetailOrderDetailsDao updateOrderDetails(RetailOrderDetailsDTO retailOrderDetailsDTO) {
        Optional<RetailOrderDetailsDao> retailOrderDetailsDao = retailOrderDetailsRepository.findById(retailOrderDetailsDTO.getOrderId());
        RetailOrderDetailsDao d = null;
        if(retailOrderDetailsDao.isPresent()) {
            d = new RetailOrderDetailsDao(retailOrderDetailsDTO);
            d.setLastUpdated(new Date(System.currentTimeMillis()));
            return retailOrderDetailsRepository.save(d);
        }
        return null;
    }*/

    /**
     * Orders placed today, using order date when present and created-on otherwise.
     * Null dates are skipped so a bad row cannot empty the dashboard.
     */
    public List<RetailOrderDao> getTodaysOrders() {
        return retailOrderRepository.findAll().stream()
                .filter(order -> {
                    Date day = order.getOrderDate() != null ? order.getOrderDate() : order.getCreatedon();
                    if (day == null) {
                        return false;
                    }
                    return DateUtil.isSameDay(new Timestamp(day.getTime()));
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
