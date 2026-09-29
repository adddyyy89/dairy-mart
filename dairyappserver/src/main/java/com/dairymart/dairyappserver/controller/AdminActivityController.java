package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.service.LoginService;
import com.dairymart.dairyappserver.service.NotificationService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin activity", description = "Platform activity feed and login sessions")
public class AdminActivityController {

    private static final Gson gson = new Gson();

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LoginService loginService;

    @Operation(summary = "Recent platform events (userId -1 notifications)")
    @GetMapping(value = "/activity", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> activity() {
        return ResponseEntity.ok(gson.toJson(notificationService.getPlatformActivity()));
    }

    @Operation(summary = "Online users and login/logout history")
    @GetMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sessions() {
        return ResponseEntity.ok(gson.toJson(loginService.getSessionOverview()));
    }
}
