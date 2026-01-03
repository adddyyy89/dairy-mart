package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.*;
import com.dairymart.dairyappserver.dto.LedgerTransactionsDTO;
import com.dairymart.dairyappserver.dto.RetailOrderDTO;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private UserService userService;

    @Autowired
    private RetailOrderService retailOrderService;

    @Autowired
    private LedgerService ledgerService;


    @CrossOrigin(origins = "*")
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAdminDashboardData() {
        logger.info("Get Admin dashboard data");

        List<UserDao> userDaos = userService.getAllUsers();
        List<RetailOrderDao> retailerOrderDaos = retailOrderService.getTodaysOrders();
        List<LedgerTransactionsDao> ledgerTransactionsDaos = ledgerService.getTodaysLedgerTransactions();

        List<RetailOrderDTO> retailOrderDTOS = new ArrayList<>(retailerOrderDaos.size());
        for(RetailOrderDao retailOrderDao : retailerOrderDaos) {
            retailOrderDTOS.add(new RetailOrderDTO(retailOrderDao));
        }

        List<LedgerTransactionsDTO> ledgerTransactionsDTOS = new ArrayList<>(ledgerTransactionsDaos.size());
        for(LedgerTransactionsDao ledgerTransactionsDao : ledgerTransactionsDaos) {
            ledgerTransactionsDTOS.add(new LedgerTransactionsDTO(ledgerTransactionsDao));
        }

        int totalRetailers = userService.getRetailers(userDaos).size();
        int totalSalesman = userService.getSalesman(userDaos).size();
        int totalTodaysOrder = retailerOrderDaos.size();
        int totalTodaysTransactions = ledgerTransactionsDaos.size();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalRetailers", totalRetailers);
        jsonObject.put("totalSalesman", totalSalesman);
        jsonObject.put("totalTodaysOrder", totalTodaysOrder);
        jsonObject.put("totalTodaysTransactions", totalTodaysTransactions);
        jsonObject.put("latestOrders", retailOrderDTOS);
        jsonObject.put("latestTransactions", ledgerTransactionsDTOS);

        logger.info("Get Admin dashboard data completed.");

        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(jsonObject));
    }



}
