package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.*;
import com.dairymart.dairyappserver.dto.*;
import com.dairymart.dairyappserver.service.*;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ledger")
public class LedgerController {

    Logger logger = LoggerFactory.getLogger(LedgerController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private UserService userService;



    @GetMapping(value = "/get/unassigned/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUnassignedRetailerList(@PathVariable String userId) {
        logger.info("Get Salesman dashboard data for salesman: " + userId);
        int salesmanId = Integer.parseInt(userId);

        List<UserDao> userDaos = ledgerService.getListToCreateLedger(salesmanId);
        List<UserDTO> userDTOs = new ArrayList<>();
        for(UserDao dao : userDaos) {
            userDTOs.add(new UserDTO(dao));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(userDTOs));
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addUser(@RequestBody LedgerDTO ledgerDTO) {
        logger.info("add ledger called.");
        if(ledgerDTO == null || ledgerDTO.getRetailerId() < 0 || ledgerDTO.getSalesmanId() < 0) {
            logger.error("Ledger details are not correct");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect ledger details.");
        }

        ledgerDTO.setActive(true);
        ledgerDTO.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        ledgerDTO.setLastUpdated(new Timestamp(System.currentTimeMillis()));

        LedgerDao ledgerDao = ledgerService.createLedger(new LedgerDao(ledgerDTO));
        if(ledgerDao != null){
            logger.info("Ledger created successfully. Salesman Id: " + ledgerDao.getSalesmanId() + ", Retailer Id: " + ledgerDao.getRetailerId());
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new LedgerDTO(ledgerDao)));
        }

        logger.error("Ledger unable to created. Salesman Id: " + ledgerDao.getSalesmanId() + ", Retailer Id: " + ledgerDao.getRetailerId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while creating ledger entry.");
    }

    @GetMapping(value = "/salesman/get/{ledgerid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSalesmanLedgerDetails(@PathVariable String ledgerid) {
        logger.info("Get Ledger details for ledgerid: " + ledgerid);
        long ledgerId = Integer.parseInt(ledgerid);

        SalesmanLedgerForRetailerDTO salesmanLedgerForRetailerDTO = ledgerService.getLedgerBalanceBySalesman(ledgerId);

        logger.info("Get Ledger details success for ledgerid: " + ledgerId);
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(salesmanLedgerForRetailerDTO));
    }

    @PostMapping(value = "/salesman/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSalesmanLedgerDetails(@RequestBody LedgerTransactionsDTO ledgerTransactionsDTO) {
        logger.info("Update Ledger Transaction for ledgerid : " + ledgerTransactionsDTO.getLedgerId());

        ledgerTransactionsDTO.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        ledgerTransactionsDTO.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        LedgerTransactionsDao ledgerTransactionsDao = ledgerService.updateSalesmanLedgerTransaction(new LedgerTransactionsDao(ledgerTransactionsDTO));

        if(ledgerTransactionsDao != null) {

        }

        logger.info("Update Ledger Transaction successful for ledgerid : " + ledgerTransactionsDTO.getLedgerId());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new LedgerTransactionsDTO(ledgerTransactionsDao)));
    }



}
