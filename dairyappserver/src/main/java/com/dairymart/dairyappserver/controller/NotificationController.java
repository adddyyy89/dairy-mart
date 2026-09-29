package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.AppNotificationDao;
import com.dairymart.dairyappserver.dto.AppNotificationDTO;
import com.dairymart.dairyappserver.service.NotificationService;
import com.dairymart.dairyappserver.util.ApiMessages;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/notification")
@Tag(name = "Notifications", description = "Inbox, broadcasts, and scheduled notices")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Inbox for one user")
    @GetMapping(value = "/get/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getNotifications(@PathVariable String userId) {
        int id = Integer.parseInt(userId);
        List<AppNotificationDTO> dtos = new ArrayList<>();
        for (AppNotificationDao dao : notificationService.getForUser(id)) {
            dtos.add(new AppNotificationDTO(dao));
        }
        logger.info("Fetched {} notifications for user {}", dtos.size(), id);
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(dtos));
    }

    @Operation(summary = "Unread count for one user")
    @GetMapping(value = "/unread/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> unreadCount(@PathVariable String userId) {
        int id = Integer.parseInt(userId);
        Map<String, Object> json = new HashMap<>();
        json.put("unread", notificationService.unreadCount(id));
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(json));
    }

    @Operation(summary = "Mark one notification as read")
    @PostMapping(value = "/read/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> markRead(@PathVariable String notificationId) {
        AppNotificationDao dao = notificationService.markRead(Long.parseLong(notificationId));
        if (dao == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiMessages.of("Notification not found"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new AppNotificationDTO(dao)));
    }

    @Operation(summary = "Send a notice now, or set scheduledForMillis to send later")
    @PostMapping(value = "/broadcast", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> broadcast(@RequestBody AppNotificationDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(notificationService.sendOrSchedule(dto)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiMessages.of(ex.getMessage()));
        }
    }

    @Operation(summary = "List scheduled notices")
    @GetMapping(value = "/scheduled", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> scheduled() {
        return ResponseEntity.ok(gson.toJson(notificationService.listScheduled()));
    }

    @Operation(summary = "Cancel a scheduled notice that has not been sent")
    @PostMapping(value = "/scheduled/{id}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> cancelScheduled(@PathVariable String id) {
        try {
            return ResponseEntity.ok(gson.toJson(notificationService.cancelScheduled(Long.parseLong(id))));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiMessages.of(ex.getMessage()));
        }
    }
}
