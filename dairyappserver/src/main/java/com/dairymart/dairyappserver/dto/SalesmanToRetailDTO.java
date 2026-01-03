package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.SalesmanToRetailDao;
import com.dairymart.dairyappserver.dao.ShopDao;
import com.dairymart.dairyappserver.dao.UserDao;
import jakarta.persistence.*;

import java.sql.Date;

public class SalesmanToRetailDTO {

    private int salesmanId;
    private int retailerId;
    private Date createdOn;
    private int createdBy;
    private String vehicleNumber;
    private Date lastUpdated;
    private int branchId;
    private Boolean isActive;

    private ShopDTO retailer;
    private UserDTO salesman;
    private BranchDTO branch;

    public SalesmanToRetailDTO() {

    }

    public SalesmanToRetailDTO(SalesmanToRetailDao dao) {
        this.salesmanId = dao.getSalesmanId();
        this.retailerId = dao.getRetailerId();
        this.retailer = new ShopDTO(dao.getRetailer());
        this.salesman = new UserDTO(dao.getSalesman());
        this.createdOn = dao.getCreatedOn();
        this.createdBy = dao.getCreatedBy();
        this.lastUpdated = dao.getLastUpdated();
        this.vehicleNumber = dao.getVehicleNumber();
        this.branchId = dao.getBranchId();
        this.isActive = dao.isActive();
        this.branch = new BranchDTO(dao.getBranch());
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

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ShopDTO getRetailer() {
        return retailer;
    }

    public void setRetailer(ShopDTO retailer) {
        this.retailer = retailer;
    }

    public UserDTO getSalesman() {
        return salesman;
    }

    public void setSalesman(UserDTO salesman) {
        this.salesman = salesman;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getActive() {
        return isActive;
    }

    public BranchDTO getBranch() {
        return branch;
    }

    public void setBranch(BranchDTO branch) {
        this.branch = branch;
    }
}
