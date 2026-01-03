package com.dairymart.dairyappexceldump.dao;

import com.dairymart.dairyappserver.dto.BranchDTO;
import com.dairymart.dairyappserver.dto.UserAddressDTO;
import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "branch", schema = "public")
public class BranchDao {

    @Id
    @Column(name = "branchid")
    @SequenceGenerator(name = "BRANCH_ID", sequenceName = "branch_seq", allocationSize = 1)
    @GeneratedValue(generator = "BRANCH_ID", strategy = GenerationType.SEQUENCE)
    private int branchId;

    @Column(name = "branchname")
    private String branchName;

    @Column(name = "createdby")
    private int createdBy;

    @Column(name = "createdon")
    private Date createdOn;

    @Column(name = "isactive")
    private Boolean isActive;

    @Column(name = "lastupdated")
    private Date lastUpdated;

    @Column(name = "addressid")
    private int addressId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "addressid", insertable = false, updatable = false)
    private UserAddressDao address;

    public BranchDao() {
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

    public UserAddressDao getAddress() {
        return address;
    }

    public void setAddress(UserAddressDao address) {
        this.address = address;
    }

}
