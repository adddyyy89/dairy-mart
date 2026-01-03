package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "ledger", schema = "public")
public class LedgerDao {

    @Id
    @Column(name = "ledgerid")
    @SequenceGenerator(name = "LEDGER_SEQ", sequenceName = "ledger_seq", allocationSize = 1)
    @GeneratedValue(generator = "LEDGER_SEQ", strategy = GenerationType.SEQUENCE)
    private long ledgerId;

    @Column(name = "salesmanid")
    private int salesmanId;

    @Column(name = "retailerid")
    private int retailerId;

    @Column(name = "createdon")
    private Timestamp createdOn;

    @Column(name = "lastupdated")
    private Timestamp lastUpdated;

    @Column(name = "isactive")
    private boolean active;

    @Column(name = "createdby")
    private int createdBy;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "salesmanid", insertable = false, updatable = false)
    private UserDao salesman;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "retailerid", insertable = false, updatable = false)
    private UserDao retailer;


    public LedgerDao() {
    }

    public long getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(long ledgerId) {
        this.ledgerId = ledgerId;
    }

    public int getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(int salesmanId) {
        this.salesmanId = salesmanId;
    }

    public int getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(int retailerId) {
        this.retailerId = retailerId;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public UserDao getSalesman() {
        return salesman;
    }

    public void setSalesman(UserDao salesman) {
        this.salesman = salesman;
    }

    public UserDao getRetailer() {
        return retailer;
    }

    public void setRetailer(UserDao retailer) {
        this.retailer = retailer;
    }

    public LedgerDao(int salesmanId, int retailerId, Timestamp createdOn, Timestamp lastUpdated, boolean active, int createdBy) {
        this.salesmanId = salesmanId;
        this.retailerId = retailerId;
        this.createdOn = createdOn;
        this.lastUpdated = lastUpdated;
        this.active = active;
        this.createdBy = createdBy;
    }
}
