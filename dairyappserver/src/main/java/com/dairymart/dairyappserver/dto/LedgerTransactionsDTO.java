package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.LedgerDao;
import com.dairymart.dairyappserver.dao.LedgerTransactionsDao;
import jakarta.persistence.*;

import java.sql.Timestamp;


public class LedgerTransactionsDTO {

    private long transactionsId;

    private long ledgerId;

    private double amount;

    private boolean credit;

    private boolean debit;

    private int paymentTypeId;

    private Timestamp createdOn;

    private Timestamp lastUpdated;

    private int createdBy;

    private LedgerDTO ledger;

    private PaymentTypeDTO paymentType;


    public LedgerTransactionsDTO() {
    }

    public LedgerTransactionsDTO(long ledgerId, double amount, boolean credit, boolean debit, int paymentTypeId, Timestamp createdOn, Timestamp lastUpdated, int createdBy) {
        this.ledgerId = ledgerId;
        this.amount = amount;
        this.credit = credit;
        this.debit = debit;
        this.paymentTypeId = paymentTypeId;
        this.createdOn = createdOn;
        this.lastUpdated = lastUpdated;
        this.createdBy = createdBy;
    }

    public long getTransactionsId() {
        return transactionsId;
    }

    public void settTransactionsId(long transactionsId) {
        this.transactionsId = transactionsId;
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

    public boolean isCredit() {
        return credit;
    }

    public void setCredit(boolean credit) {
        this.credit = credit;
    }

    public boolean isDebit() {
        return debit;
    }

    public void setDebit(boolean debit) {
        this.debit = debit;
    }

    public int getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public LedgerDTO getLedger() {
        return ledger;
    }

    public void setLedger(LedgerDTO ledger) {
        this.ledger = ledger;
    }

    public LedgerTransactionsDTO(LedgerTransactionsDao dao) {
        this.transactionsId = dao.getTransactionId();
        this.ledgerId = dao.getLedgerId();
        this.amount = dao.getAmount();
        this.credit = dao.isCredit();
        this.debit = dao.isDebit();
        this.paymentTypeId = dao.getPaymentTypeId();
        this.createdOn = dao.getCreatedOn();
        this.createdBy = dao.getCreatedBy();
        this.lastUpdated = dao.getLastUpdated();
        if(dao.getLedger() != null) {
            this.ledger = new LedgerDTO(dao.getLedger());
        }
        if(dao.getPaymentType() != null) {
            this.paymentType = new PaymentTypeDTO(dao.getPaymentType());
        }

    }

    public PaymentTypeDTO getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentTypeDTO paymentType) {
        this.paymentType = paymentType;
    }
}
