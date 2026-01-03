package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.LedgerDao;
import com.dairymart.dairyappserver.dao.UserDao;
import jakarta.persistence.*;

import java.sql.Timestamp;

public class LedgerDTO {

    private long ledgerId;

    private int salesmanId;

    private int retailerId;

    private Timestamp createdOn;

    private Timestamp lastUpdated;

    private boolean active;

    private int createdBy;

    private UserDTO salesman;

    private UserDTO retailer;


    public LedgerDTO() {
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

    public UserDTO getSalesman() {
        return salesman;
    }

    public void setSalesman(UserDTO salesman) {
        this.salesman = salesman;
    }

    public UserDTO getRetailer() {
        return retailer;
    }

    public void setRetailer(UserDTO retailer) {
        this.retailer = retailer;
    }

    public LedgerDTO(int salesmanId, int retailerId, Timestamp createdOn, Timestamp lastUpdated, boolean active, int createdBy) {
        this.salesmanId = salesmanId;
        this.retailerId = retailerId;
        this.createdOn = createdOn;
        this.lastUpdated = lastUpdated;
        this.active = active;
        this.createdBy = createdBy;
    }

    public LedgerDTO(LedgerDao dao) {
        this.salesmanId = dao.getSalesmanId();
        this.retailerId = dao.getRetailerId();
        this.createdOn = dao.getCreatedOn();
        this.lastUpdated = dao.getLastUpdated();
        this.active = dao.isActive();
        this.createdBy = dao.getCreatedBy();
        this.ledgerId = dao.getLedgerId();
        if(dao.getSalesman() != null) {
            this.salesman = new UserDTO(dao.getSalesman());
        }
        if(dao.getRetailer() != null) {
            this.retailer = new UserDTO(dao.getRetailer());
        }
    }
}
