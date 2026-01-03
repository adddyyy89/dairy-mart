package com.dairymart.dairyappserver.scheduler;

import com.dairymart.dairyappserver.service.LedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyLedgerScheduler {

    @Autowired
    LedgerService ledgerService;

    @Scheduled(cron = "1 0 0 * * ?")    // Every 12 AM IST to update Daily Ledger
    public void updateDailyLedger() {
        ledgerService.updateDailyLedger();
    }
}
