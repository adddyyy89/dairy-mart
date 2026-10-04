package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.service.AnalyticsService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/analytics")
@Tag(name = "Admin analytics", description = "Order mix, collections, and crate locations")
public class AdminAnalyticsController {

    private static final Logger logger = LoggerFactory.getLogger(AdminAnalyticsController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private AnalyticsService analyticsService;

    @Operation(summary = "Top products, stores, collections, and crate holders")
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> get() {
        logger.info("Get admin analytics");
        return ResponseEntity.ok(gson.toJson(analyticsService.build()));
    }
}
