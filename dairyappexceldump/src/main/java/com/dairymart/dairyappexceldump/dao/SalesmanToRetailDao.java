package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "salesmantoretail", schema = "public")
@IdClass(SalesmanToRetailId.class)
public class SalesmanToRetailDao {

    @Id
    @Column(name = "salesmanid")
    private int salesmanId;

    @Id
    @Column(name = "retailerid")
    private int retailerId;

    @Column(name = "createdon")
    private Date createdOn;

    @Column(name = "createdby")
    private int createdBy;

    @Column(name = "vehiclenumber")
    private String vehicleNumber;

    @Column(name = "lastupdated")
    private Date lastUpdated;

    @Column(name = "branchid")
    private int branchId;

    @Column(name = "isactive")
    private Boolean isActive;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "retailerid", insertable = false, updatable = false)
    private ShopDao retailer;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "salesmanid", insertable = false, updatable = false)
    private UserDao salesman;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "branchid", insertable = false, updatable = false)
    private BranchDao branch;

    public SalesmanToRetailDao() {

    }

    public SalesmanToRetailDao(int salesmanId, int retailerId, Date createdOn, int createdBy, String vehicleNumber, Date lastUpdated, UserDao retailer, int branchId) {
        this.salesmanId = salesmanId;
        this.retailerId = retailerId;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.vehicleNumber = vehicleNumber;
        this.lastUpdated = lastUpdated;
        this.branchId = branchId;
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

    public ShopDao getRetailer() {
        return retailer;
    }

    public void setRetailer(ShopDao retailer) {
        this.retailer = retailer;
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

    public UserDao getSalesman() {
        return salesman;
    }

    public void setSalesman(UserDao salesman) {
        this.salesman = salesman;
    }

    public Boolean getActive() {
        return isActive;
    }

    public BranchDao getBranch() {
        return branch;
    }

    public void setBranch(BranchDao branch) {
        this.branch = branch;
    }


}
