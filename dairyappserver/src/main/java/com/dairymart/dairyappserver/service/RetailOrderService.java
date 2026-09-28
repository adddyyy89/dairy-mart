package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.controller.RetailOrderController;
import com.dairymart.dairyappserver.dao.*;
import com.dairymart.dairyappserver.dto.ProductDTO;
import com.dairymart.dairyappserver.dto.RetailOrderDTO;
import com.dairymart.dairyappserver.dto.RetailOrderDetailsDTO;
import com.dairymart.dairyappserver.repository.OrderStatusRepository;
import com.dairymart.dairyappserver.repository.ProductRepository;
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

        logger.info("Retail order entry created with order Id: {}", savedOrderDao.getOrderId());
        logger.info("Proceeding with entries for order details..");

        if (order.getOrderDetails() != null) {
            for (RetailOrderDetailsDTO dto : order.getOrderDetails()) {
                if (dto == null || dto.getProductCode() == null || dto.getProductCode().isBlank()) {
                    continue;
                }
                dto.setLastUpdated(new Date(System.currentTimeMillis()));
                dto.setOrderId(savedOrderDao.getOrderId());
                retailOrderDetailsRepository.save(new RetailOrderDetailsDao(dto));
                logger.info("Added new entry for order Id: {}, product code: {}", dto.getOrderId(), dto.getProductCode());
            }
        }

        logger.info("Retail order details entry are completed successfully.");

        return findById(savedOrderDao.getOrderId());
    }

    @Transactional
    public RetailOrderDao updateOrder(RetailOrderDTO order) {

        Optional<RetailOrderDao> actualRetailOrderOpt = retailOrderRepository.findById(order.getOrderId());
        RetailOrderDao orderDao = actualRetailOrderOpt.get();

        orderDao.setLastUpdated(new Date(System.currentTimeMillis()));
        orderDao.setOrderStatusId(order.getOrderStatusId());


        // Save Initial Order
        RetailOrderDao savedOrderDao = retailOrderRepository.save(orderDao);

        logger.info("Retail order update entry created with order Id: {}", savedOrderDao.getOrderId());
        logger.info("Proceeding with entries for order details..");

        // Save order details one at a time
        if(order.getOrderDetails() != null) {
            for(RetailOrderDetailsDTO dto : order.getOrderDetails()) {
                dto.setLastUpdated(new Date(System.currentTimeMillis()));
                retailOrderDetailsRepository.save(new RetailOrderDetailsDao(dto));
                logger.info("Update entry for order Id: {}, product code: {}", dto.getOrderId(), dto.getProductCode());
            }
        }

        logger.info("Retail order details updated successfully.");

        return findById(savedOrderDao.getOrderId());
    }

    public RetailOrderDao findById(int id) {
        Optional<RetailOrderDao> dao = retailOrderRepository.findById(id);
        return dao.orElse(null);
    }

    public List<OrderStatusDao> getAllOrderStatus() {
        return orderStatusRepository.findAll();
    }


    public RetailOrderDao updateOrderStatus(RetailOrderDTO retailOrderDTO) {
        Optional<RetailOrderDao> dao = retailOrderRepository.findById(retailOrderDTO.getOrderId());
        RetailOrderDao d = null;
        if(dao.isPresent()) {
            RetailOrderDao retailOrderDao = dao.get();
            retailOrderDao.setLastUpdated(new Date(System.currentTimeMillis()));
            retailOrderDao.setOrderStatusId(retailOrderDTO.getOrderStatusId());
            d = retailOrderRepository.save(retailOrderDao);
        }
        return d;
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

    public List<RetailOrderDao> getTodaysOrders() {
        List<RetailOrderDao> retailerOrdersDaos = retailOrderRepository.findAll().stream().filter(x -> DateUtil.isSameDay(new Timestamp(x.getCreatedon().getTime()))).collect(Collectors.toCollection(ArrayList::new));
        return retailerOrdersDaos;
    }

}
