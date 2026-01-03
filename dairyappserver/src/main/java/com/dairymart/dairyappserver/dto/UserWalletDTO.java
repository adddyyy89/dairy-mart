package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dao.UserWalletDao;
import jakarta.persistence.*;

import java.sql.Date;

public class UserWalletDTO {

    private int walletId;

    private int userId;

    private double balance;

    private double outstanding;

    private Date createdOn;

    private Date lastUpdated;

    private int createdBy;

    private UserDTO user;


    public UserWalletDTO() {
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getOutstanding() {
        return outstanding;
    }

    public void setOutstanding(double outstanding) {
        this.outstanding = outstanding;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO owner) {
        this.user = owner;
    }

    public UserWalletDTO(int userId, double balance, double outstanding, Date createdOn, Date lastUpdated, int createdBy, UserDTO owner) {
        this.userId = userId;
        this.balance = balance;
        this.outstanding = outstanding;
        this.createdOn = createdOn;
        this.lastUpdated = lastUpdated;
        this.createdBy = createdBy;
        this.user = owner;
    }

    public UserWalletDTO(UserWalletDao dao) {
        this.balance = dao.getBalance();
        this.createdBy = dao.getCreatedBy();
        this.user = new UserDTO(dao.getUser());
        this.createdOn = dao.getCreatedOn();
        this.lastUpdated = dao.getLastUpdated();
        this.outstanding = dao.getOutstanding();
        this.userId = dao.getUserId();
        this.walletId = dao.getWalletId();
    }
}
