package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.BranchDao;
import jakarta.persistence.Column;

import java.sql.Date;

public class BranchDTO {

    private int branchId;

    private String branchName;

    private int createdBy;

    private Date createdOn;

    private Boolean isActive;

    private Date lastUpdated;

    private int addressId;

    private UserAddressDTO address;

    public BranchDTO(int branchId, String branchName, int createdBy, Date createdOn, Boolean isActive, Date lastUpdated, int addressId) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.isActive = isActive;
        this.lastUpdated = lastUpdated;
        this.addressId = addressId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public UserAddressDTO getAddress() {
        return address;
    }

    public void setAddress(UserAddressDTO address) {
        this.address = address;
    }

    public BranchDTO(BranchDao dao) {
        this.branchId = dao.getBranchId();
        this.branchName = dao.getBranchName();
        this.addressId = dao.getAddressId();
        this.createdBy = dao.getCreatedBy();
        this.createdOn = dao.getCreatedOn();
        if(dao.getAddress() != null)
            this.address = new UserAddressDTO(dao.getAddress());
        this.isActive = dao.getActive();
        this.lastUpdated = dao.getLastUpdated();
    }
}
