package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.DailyLedgerDao;
import com.dairymart.dairyappserver.dao.DailyLedgerId;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dao.UserWalletDao;
import jakarta.persistence.*;

import java.sql.Timestamp;

public class DailyLedgerDTO {


    private int userId;

    private Timestamp recordTimestamp;

    private double startingWalletBalance;

    private double startingOutstandingBalance;

    private double walletBalance;

    private double outstandingBalance;

    private double totalBalance;

    private Timestamp lastUpdated;

    private int createdBy;

    private int walletId;

    private UserDTO user;

    private UserWalletDTO wallet;


    public DailyLedgerDTO() {
    }

    public DailyLedgerDTO(int userId, Timestamp recordTimestamp, double startingWalletBalance, double startingOutstandingBalance, double walletBalance, double outstandingBalance, double totalBalance, Timestamp lastUpdated, int createdBy, int walletId) {
        this.userId = userId;
        this.recordTimestamp = recordTimestamp;
        this.startingWalletBalance = startingWalletBalance;
        this.startingOutstandingBalance = startingOutstandingBalance;
        this.walletBalance = walletBalance;
        this.outstandingBalance = outstandingBalance;
        this.totalBalance = totalBalance;
        this.lastUpdated = lastUpdated;
        this.createdBy = createdBy;
        this.walletId = walletId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getRecordTimestamp() {
        return recordTimestamp;
    }

    public void setRecordTimestamp(Timestamp recordTimestamp) {
        this.recordTimestamp = recordTimestamp;
    }

    public double getStartingWalletBalance() {
        return startingWalletBalance;
    }

    public void setStartingWalletBalance(double startingWalletBalance) {
        this.startingWalletBalance = startingWalletBalance;
    }

    public double getStartingOutstandingBalance() {
        return startingOutstandingBalance;
    }

    public void setStartingOutstandingBalance(double startingOutstandingBalance) {
        this.startingOutstandingBalance = startingOutstandingBalance;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public double getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(double outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
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

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public UserWalletDTO getWallet() {
        return wallet;
    }

    public void setWallet(UserWalletDTO wallet) {
        this.wallet = wallet;
    }

    public DailyLedgerDTO(DailyLedgerDao dao) {
        this.userId = dao.getUserId();
        this.recordTimestamp = dao.getRecordTimestamp();
        this.startingWalletBalance = dao.getStartingWalletBalance();
        this.startingOutstandingBalance = dao.getStartingOutstandingBalance();
        this.walletBalance = dao.getWalletBalance();
        this.outstandingBalance = dao.getOutstandingBalance();
        this.totalBalance = dao.getTotalBalance();
        this.lastUpdated = dao.getLastUpdated();
        this.createdBy = dao.getCreatedBy();
        this.walletId = dao.getWalletId();
        if(dao.getUser() != null) {
            this.user = new UserDTO(dao.getUser());
        }
        if(dao.getWallet() != null) {
            this.wallet = new UserWalletDTO(dao.getWallet());
        }

    }
        
}
