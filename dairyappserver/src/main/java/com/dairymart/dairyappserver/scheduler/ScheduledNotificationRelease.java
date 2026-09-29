package com.dairymart.dairyappserver.scheduler;

import com.dairymart.dairyappserver.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledNotificationRelease {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledNotificationRelease.class);

    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedDelay = 30000)
    public void releaseDue() {
        try {
            int released = notificationService.releaseDueScheduled();
            if (released > 0) {
                logger.info("Released {} scheduled notification(s)", released);
            }
        } catch (Exception ex) {
            logger.warn("Could not release scheduled notifications: {}", ex.getMessage());
        }
    }
}
