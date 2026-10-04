package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.ScheduledNotificationDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledNotificationRepository extends JpaRepository<ScheduledNotificationDao, Long> {
}
