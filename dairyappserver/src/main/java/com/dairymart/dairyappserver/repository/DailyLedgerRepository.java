package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.DailyLedgerDao;
import com.dairymart.dairyappserver.dao.DailyLedgerId;
import com.dairymart.dairyappserver.dao.LedgerDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyLedgerRepository extends JpaRepository<DailyLedgerDao, DailyLedgerId> {

}
