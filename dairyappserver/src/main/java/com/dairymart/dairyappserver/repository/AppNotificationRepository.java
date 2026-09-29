package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.AppNotificationDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppNotificationRepository extends JpaRepository<AppNotificationDao, Long> {
}
