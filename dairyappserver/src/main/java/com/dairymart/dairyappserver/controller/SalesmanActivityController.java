package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.LedgerTransactionsDao;
import com.dairymart.dairyappserver.dao.SalesmanOrdersDao;
import com.dairymart.dairyappserver.dao.UserWalletDao;
import com.dairymart.dairyappserver.dto.LedgerTransactionsDTO;
import com.dairymart.dairyappserver.service.CrateService;
import com.dairymart.dairyappserver.service.LedgerService;
import com.dairymart.dairyappserver.service.SalesmanToRetailService;
import com.dairymart.dairyappserver.service.UserWalletService;
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
@RequestMapping("/salesman/activity")
public class SalesmanActivityController {

    Logger logger = LoggerFactory.getLogger(SalesmanActivityController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private SalesmanToRetailService salesmanToRetailService;

    @Autowired
    private CrateService crateService;

    @Autowired
    private LedgerService ledgerService;



    @GetMapping(value = "/get/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSalesmanDashboardData(@PathVariable String userId) {
        logger.info("Get Salesman dashboard data for salesman: " + userId);
        int salesmanId = Integer.parseInt(userId);

        UserWalletDao walletDao = userWalletService.getWalletDetails(salesmanId);


        List<SalesmanOrdersDao> salesmanOrdersDaos = salesmanToRetailService.getCurrentOrdersPlaced(salesmanId);

        int cratesAssigned = crateService.getCurrentAssignedCrateForUser(salesmanId);

        List<LedgerTransactionsDao> ledgerTransactionsDaos = ledgerService.getSalesmanDashboardTransactions(salesmanId);
        List<LedgerTransactionsDTO> ledgerTransactionsDTOS = new ArrayList<>();
        for(LedgerTransactionsDao ledgerTransactionsDao : ledgerTransactionsDaos) {
            ledgerTransactionsDTOS.add(new LedgerTransactionsDTO(ledgerTransactionsDao));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("walletbalance", walletDao != null ? walletDao.getBalance() : "??");
        jsonObject.put("ordersplaced", salesmanOrdersDaos.size());
        jsonObject.put("cratesassigned", cratesAssigned);
        jsonObject.put("recenttransactions", ledgerTransactionsDTOS);

        logger.info("Get Salesman dashboard data for salesman: " + userId + ", completed.");

        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(jsonObject));
    }



}
