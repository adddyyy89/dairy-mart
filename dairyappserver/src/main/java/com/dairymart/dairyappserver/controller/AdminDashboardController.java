package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.*;
import com.dairymart.dairyappserver.dto.LedgerTransactionsDTO;
import com.dairymart.dairyappserver.dto.RetailOrderDTO;
import com.dairymart.dairyappserver.service.*;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin dashboard", description = "Today’s orders, status mix, and recent activity")
public class AdminDashboardController {

    Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private UserService userService;

    @Autowired
    private RetailOrderService retailOrderService;

    @Autowired
    private LedgerService ledgerService;


    /**
     * KPI counts plus today's orders and ledger rows for the AdminLTE home page.
     */
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

        int statusNew = 0;
        int statusConfirmed = 0;
        int statusRejected = 0;
        int statusDispatched = 0;
        int statusDelivered = 0;
        int statusOther = 0;
        for (RetailOrderDao order : retailerOrderDaos) {
            switch (order.getOrderStatusId()) {
                case 1 -> statusNew++;
                case 2 -> statusConfirmed++;
                case 3 -> statusRejected++;
                case 4 -> statusDispatched++;
                case 5 -> statusDelivered++;
                default -> statusOther++;
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalRetailers", totalRetailers);
        jsonObject.put("totalSalesman", totalSalesman);
        jsonObject.put("totalTodaysOrder", totalTodaysOrder);
        jsonObject.put("totalTodaysTransactions", totalTodaysTransactions);
        jsonObject.put("statusNew", statusNew);
        jsonObject.put("statusConfirmed", statusConfirmed);
        jsonObject.put("statusRejected", statusRejected);
        jsonObject.put("statusDispatched", statusDispatched);
        jsonObject.put("statusDelivered", statusDelivered);
        jsonObject.put("statusOther", statusOther);
        jsonObject.put("latestOrders", retailOrderDTOS);
        jsonObject.put("latestTransactions", ledgerTransactionsDTOS);

        logger.info("Get Admin dashboard data completed.");

        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(jsonObject));
    }



}
