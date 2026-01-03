package com.dairymart.dairyappserver.dao;

import com.dairymart.dairyappserver.dto.LedgerDTO;
import com.dairymart.dairyappserver.dto.LedgerTransactionsDTO;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "ledgertransactions", schema = "public")
public class LedgerTransactionsDao {

    @Id
    @Column(name = "transactionid")
    @SequenceGenerator(name = "LEDGERTRANSACTIONS_SEQ", sequenceName = "ledgertransactions_seq", allocationSize = 1)
    @GeneratedValue(generator = "LEDGERTRANSACTIONS_SEQ", strategy = GenerationType.SEQUENCE)
    private long transactionId;

    @Column(name = "ledgerid")
    private long ledgerId;

    @Column(name = "amount")
    private double amount;

    @Column(name = "iscredit")
    private boolean credit;

    @Column(name = "isdebit")
    private boolean debit;

    @Column(name = "paymenttypeid")
    private int paymentTypeId;

    @Column(name = "createdon")
    private Timestamp createdOn;

    @Column(name = "lastupdated")
    private Timestamp lastUpdated;

    @Column(name = "createdby")
    private int createdBy;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ledgerid", insertable = false, updatable = false)
    private LedgerDao ledger;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "paymenttypeid", insertable = false, updatable = false)
    private PaymentTypeDao paymentType;


    public LedgerTransactionsDao() {
    }

    public LedgerTransactionsDao(long ledgerId, double amount, boolean credit, boolean debit, int paymentTypeId, Timestamp createdOn, Timestamp lastUpdated, int createdBy) {
        this.ledgerId = ledgerId;
        this.amount = amount;
        this.credit = credit;
        this.debit = debit;
        this.paymentTypeId = paymentTypeId;
        this.createdOn = createdOn;
        this.lastUpdated = lastUpdated;
        this.createdBy = createdBy;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionsId(long ledgerTransactionsId) {
        this.transactionId = ledgerTransactionsId;
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

    public LedgerDao getLedger() {
        return ledger;
    }

    public void setLedger(LedgerDao ledger) {
        this.ledger = ledger;
    }

    public LedgerTransactionsDao(LedgerTransactionsDTO dto) {
        this.transactionId = dto.getTransactionsId();
        this.ledgerId = dto.getLedgerId();
        this.amount = dto.getAmount();
        this.credit = dto.isCredit();
        this.debit = dto.isDebit();
        this.paymentTypeId = dto.getPaymentTypeId();
        this.createdOn = dto.getCreatedOn();
        this.createdBy = dto.getCreatedBy();
        this.lastUpdated = dto.getLastUpdated();
    }

    public PaymentTypeDao getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentTypeDao paymentType) {
        this.paymentType = paymentType;
    }
}
