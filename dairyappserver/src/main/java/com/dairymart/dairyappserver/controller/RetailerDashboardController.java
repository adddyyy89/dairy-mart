package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.LedgerTransactionsDao;
import com.dairymart.dairyappserver.dao.RetailOrderDao;
import com.dairymart.dairyappserver.dao.SalesmanOrdersDao;
import com.dairymart.dairyappserver.dao.UserWalletDao;
import com.dairymart.dairyappserver.dto.LedgerTransactionsDTO;
import com.dairymart.dairyappserver.service.*;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/retailer/dashboard")
public class RetailerDashboardController {

    Logger logger = LoggerFactory.getLogger(RetailerDashboardController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private RetailOrderService retailOrderService;

    @Autowired
    private CrateService crateService;

    @Autowired
    private LedgerService ledgerService;



    @GetMapping(value = "/get/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRetailerDashboardData(@PathVariable String userId) {
        logger.info("Get Salesman dashboard data for salesman: " + userId);


        int retailerId = Integer.parseInt(userId);

        UserWalletDao walletDao = userWalletService.getWalletDetails(retailerId);


        List<RetailOrderDao> retailOrderDaos = retailOrderService.getCurrentOrdersPlaced(retailerId);

        int cratesAssigned = crateService.getCurrentAssignedCrateForUser(retailerId);

        List<LedgerTransactionsDao> ledgerTransactionsDaos = ledgerService.getRetailerDashboardTransactions(retailerId);
        List<LedgerTransactionsDTO> ledgerTransactionsDTOS = new ArrayList<>();
        for(LedgerTransactionsDao ledgerTransactionsDao : ledgerTransactionsDaos) {
            ledgerTransactionsDTOS.add(new LedgerTransactionsDTO(ledgerTransactionsDao));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("walletbalance", walletDao != null ? walletDao.getBalance() : 0);
        jsonObject.put("ordersplaced", retailOrderDaos.size());
        jsonObject.put("cratesassigned", cratesAssigned);
        jsonObject.put("recenttransactions", ledgerTransactionsDTOS);

        logger.info("Get Retailer dashboard data for retailer : " + userId + ", completed.");

        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(jsonObject));
    }



}
