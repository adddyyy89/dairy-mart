package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.LedgerDao;
import com.dairymart.dairyappserver.dao.LedgerTransactionsDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.LedgerDTO;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.service.LedgerService;
import com.dairymart.dairyappserver.service.UserService;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/ledgers")
public class AdminLedgerController {

    Logger logger = LoggerFactory.getLogger(AdminLedgerController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private LedgerService ledgerService;


    @CrossOrigin(origins = "*")
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAdminLedgerData() {
        logger.info("Get Admin Ledger data");

        List<LedgerTransactionsDao> ledgerTransactionsDaos = ledgerService.getAllLedgerTransactions();
        List<LedgerDao> ledgerDaos = ledgerService.getAllLedgers();

        Map<Long, Double> ledgerCreditMap = new HashMap<>();
        Map<Long, Double> ledgerDebitMap = new HashMap<>();

        for(LedgerDao ledgerDao : ledgerDaos){

            double credit = 0;
            double debit = 0;
            for(LedgerTransactionsDao ledgerTransactionsDao : ledgerTransactionsDaos) {
                if(ledgerTransactionsDao.getLedgerId() == ledgerDao.getLedgerId()) {
                    if(ledgerTransactionsDao.isDebit()) {
                        debit+=ledgerTransactionsDao.getAmount();
                    }
                    if(ledgerTransactionsDao.isCredit()) {
                        credit+=ledgerTransactionsDao.getAmount();
                    }
                }
            }

            ledgerDebitMap.put(ledgerDao.getLedgerId(), debit);
            ledgerCreditMap.put(ledgerDao.getLedgerId(), credit);
        }

        List<LedgerDTO> ledgerDTOS = new ArrayList<>(ledgerDaos.size());
        for(LedgerDao ledgerDao : ledgerDaos) {
            ledgerDTOS.add(new LedgerDTO(ledgerDao));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ledger", ledgerDTOS);
        jsonObject.put("creditmap", ledgerCreditMap);
        jsonObject.put("debitmap", ledgerDebitMap);

        logger.info("Get Admin ledger data completed.");

        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(jsonObject));
    }



}
