package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.LedgerTransactionsDao;

import java.sql.Timestamp;


public class LedgerAmountDTO {

    private long ledgerId;

    private double amount;

    private LedgerDTO ledger;


    public LedgerAmountDTO() {
    }

    public LedgerAmountDTO(long ledgerId, double amount, boolean credit, boolean debit, int paymentTypeId, Timestamp createdOn, Timestamp lastUpdated, int createdBy) {
        this.ledgerId = ledgerId;
        this.amount = amount;
    }


    public long getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(long ledgerId) {
        this.ledgerId = ledgerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LedgerDTO getLedger() {
        return ledger;
    }

    public void setLedger(LedgerDTO ledger) {
        this.ledger = ledger;
    }

    public LedgerAmountDTO(LedgerTransactionsDao dao) {
        this.ledgerId = dao.getLedgerId();
        this.amount = dao.getAmount();
        if(dao.getLedger() != null) {
            this.ledger = new LedgerDTO(dao.getLedger());
        }


    }
}
