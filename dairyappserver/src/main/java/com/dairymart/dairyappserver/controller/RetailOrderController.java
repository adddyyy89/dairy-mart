package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.*;
import com.dairymart.dairyappserver.dto.ProductDTO;
import com.dairymart.dairyappserver.dto.RetailOrderDTO;
import com.dairymart.dairyappserver.dto.SalesmanToRetailDTO;
import com.dairymart.dairyappserver.service.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/retailorder")
public class RetailOrderController {

    Logger logger = LoggerFactory.getLogger(RetailOrderController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private RetailOrderService retailOrderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SalesmanToRetailService salesmanToRetailService;

    @Autowired
    private ShopService shopService;

    @GetMapping(value = "/get/all/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllProducts() {
        logger.info("Get all products called.");
        List<ProductDao> daoList = productService.getAllProducts();
        logger.info("Fetched total products: {}", daoList.size());
        List<ProductDTO> productDTOS = new ArrayList<>();
        for(ProductDao dao : daoList) {
            productDTOS.add(new ProductDTO(dao));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(productDTOS));

    }

    @GetMapping(value = "/get/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllRetailOrders() {
        logger.info("Get all retail orders called.");
        List<RetailOrderDao> daoList = retailOrderService.getAllOrders();
        logger.info("Fetched total records: {}", daoList.size());
        List<RetailOrderDTO> dtoList = new ArrayList<>();
        for(RetailOrderDao dao : daoList) {
            dtoList.add(new RetailOrderDTO(dao));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(dtoList));
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addRetailOrder(@RequestBody RetailOrderDTO retailOrder) {
        logger.info("add retail order called. Order = {}", retailOrder);

        RetailOrderDao retailOrderDao = retailOrderService.createOrder(retailOrder);

        logger.info("retail order call is completed successfully.");

        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new RetailOrderDTO(retailOrderDao)));

    }

    /*@PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeSalesmanToRetailAssignment(@RequestBody SalesmanToRetailDTO salesmanToRetailDTO) {
        logger.info("Remove Salesman to Retail assignment called.");
        boolean status = salesmanToRetailService.removeSalesmanToRetailAssignment(salesmanToRetailDTO.getSalesmanId(), salesmanToRetailDTO.getRetailerId());
        if(status) {
            logger.info("Successfully removed salesmanId : {}, retailerId : {}", salesmanToRetailDTO.getSalesmanId(), salesmanToRetailDTO.getRetailerId());
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson("success"));
        }
        logger.error("Unable to remove assignment.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson("error"));
    }*/

    @GetMapping(value = "/get/salesman/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOrdersBySalesmanId(@PathVariable String id) {
        logger.info("Get orders for salesman id : {} called.", id);
        int salesmanId = -1;
        try{
            salesmanId = Integer.parseInt(id);
        } catch(NumberFormatException ex){
            logger.error("Invalid salesman id provided.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson("Invalid salesman id provided."));
        }
        List<SalesmanToRetailDao> salesmanToRetailDaos = salesmanToRetailService.getAllRetailsforSalesman(salesmanId);
        List<RetailOrderDao> retailOrderDaos = retailOrderService.getOrdersForRetailers(salesmanToRetailDaos.stream().map(SalesmanToRetailDao::getRetailerId).collect(Collectors.toList()));
        List<RetailOrderDTO> retailOrderDTOS = new ArrayList<>();
        for(RetailOrderDao d : retailOrderDaos) {
            retailOrderDTOS.add(new RetailOrderDTO(d));
        }
        logger.info("Fetched orders for salesman : {}, Total Orders: {}", id, retailOrderDTOS.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(retailOrderDTOS));
    }

    @GetMapping(value = "/get/retailer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOrdersByRetailerId(@PathVariable String id) {
        logger.info("Get orders for user id : {} called.", id);

        List<ShopDao> shopDaos = shopService.getShopByRetailerId(Integer.parseInt(id));
        List<Integer> retailerIdList = new ArrayList<>();
        for(ShopDao shopDao : shopDaos) {
            retailerIdList.add(shopDao.getShopId());
        }

        List<RetailOrderDao> retailOrderDaos = retailOrderService.getOrdersForRetailers(retailerIdList);
        List<RetailOrderDTO> retailOrderDTOS = new ArrayList<>();
        for(RetailOrderDao d : retailOrderDaos) {
            retailOrderDTOS.add(new RetailOrderDTO(d));
        }
        logger.info("Fetched orders for retailer : {}, Total Orders: {}", id, retailOrderDTOS.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(retailOrderDTOS));
    }

}
