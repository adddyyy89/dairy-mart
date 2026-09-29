package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dto.SalesmanWalletSummaryDTO;
import com.dairymart.dairyappserver.service.LedgerService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.List;

@RestController
@RequestMapping("/admin/wallets")
@Tag(name = "Admin wallets", description = "Salesman wallet summaries")
public class AdminWalletController {

    private static final Logger logger = LoggerFactory.getLogger(AdminWalletController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private LedgerService ledgerService;

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/salesmen", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSalesmanWallets() {
        List<SalesmanWalletSummaryDTO> summaries = ledgerService.getSalesmanWalletSummaries();
        logger.info("Salesman wallet summaries: {} row(s)", summaries.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(summaries));
    }
}
