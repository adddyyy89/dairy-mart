package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.SalesmanToRetailDao;
import com.dairymart.dairyappserver.dao.ShopDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.SalesmanToRetailDTO;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.service.SalesmanToRetailService;
import com.dairymart.dairyappserver.service.ShopService;
import com.dairymart.dairyappserver.service.UserService;
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

@RestController
@RequestMapping("/salesmantoretail")
public class SalesmanToRetailController {

    Logger logger = LoggerFactory.getLogger(SalesmanToRetailController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private SalesmanToRetailService salesmanToRetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private ShopService shopService;

    @PostMapping(value = "/assign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addNewSalesmanToRetail(@RequestBody SalesmanToRetailDTO salesmanToRetailDTO) {
        logger.info("Assign new retailer to salesman called.");
        if(salesmanToRetailDTO == null || salesmanToRetailDTO.getSalesmanId() <= 0 || salesmanToRetailDTO.getRetailerId() <= 0) {
            logger.error("Assignment details not provided correctly.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Assignment details are incorrect.");
        }

        UserDao salesman = userService.findById(salesmanToRetailDTO.getSalesmanId());
        ShopDao retailer = shopService.findById(salesmanToRetailDTO.getRetailerId());
        if(salesman == null || !salesman.getType().getUserTypeDesc().equalsIgnoreCase("salesman") ||
        retailer == null) {
            logger.error("Salesman or Retailer details are not correct. SalesmanId: {}, RetailerId(Shop): {}", salesmanToRetailDTO.getSalesmanId(), salesmanToRetailDTO.getRetailerId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Salesman or Retailer details are not correct");
        }

        salesmanToRetailDTO.setLastUpdated(new Date(System.currentTimeMillis()));
        salesmanToRetailDTO.setCreatedOn(new Date(System.currentTimeMillis()));

        SalesmanToRetailDao dao = salesmanToRetailService.assignNewSalesToRetail(new SalesmanToRetailDao(salesmanToRetailDTO));
        if(dao != null) {
            logger.info("New assignment is created for salesmanid: {} and retailerid: {}",dao.getSalesmanId(), dao.getRetailerId());
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(salesmanToRetailDTO));
        }

        logger.error("Unable to update salesman and retail details.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to update salesman and retail details.");

    }

    @GetMapping(value = "/get/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllSalesmanToRetail() {
        logger.info("Get all salesman to retail details called.");
        List<SalesmanToRetailDao> daoList = salesmanToRetailService.getAllSalestoRetail();
        logger.info("Fetched total records: {}", daoList.size());
        List<SalesmanToRetailDTO> dtoList = new ArrayList<>();
        for(SalesmanToRetailDao dao : daoList) {
            dtoList.add(new SalesmanToRetailDTO(dao));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(dtoList));
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeSalesmanToRetailAssignment(@RequestBody SalesmanToRetailDTO salesmanToRetailDTO) {
        logger.info("Remove Salesman to Retail assignment called.");
        boolean status = salesmanToRetailService.removeSalesmanToRetailAssignment(salesmanToRetailDTO.getSalesmanId(), salesmanToRetailDTO.getRetailerId());
        if(status) {
            logger.info("Successfully removed salesmanId : {}, retailerId : {}", salesmanToRetailDTO.getSalesmanId(), salesmanToRetailDTO.getRetailerId());
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson("success"));
        }
        logger.error("Unable to remove assignment.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson("error"));
    }

    @GetMapping(value = "/get/assignment/salesman/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAssignmentsBySalesmanId(@PathVariable String id) {
        logger.info("Get assignments for salesman id : {} called.", id);
        int salesmanId = -1;
        try{
            salesmanId = Integer.parseInt(id);
        } catch(NumberFormatException ex){
            logger.error("Invalid salesman id provided.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson("Invalid salesman id provided."));
        }
        List<SalesmanToRetailDao> daoList = salesmanToRetailService.getAllRetailsforSalesman(salesmanId);
        List<SalesmanToRetailDTO> dtoList = new ArrayList<>();
        for(SalesmanToRetailDao dao : daoList) {
            dtoList.add(new SalesmanToRetailDTO(dao));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(dtoList));
    }

}
