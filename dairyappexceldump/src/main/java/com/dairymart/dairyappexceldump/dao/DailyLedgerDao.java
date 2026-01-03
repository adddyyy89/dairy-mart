package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "dailyledger", schema = "public")
@IdClass(DailyLedgerId.class)
public class DailyLedgerDao {

    @Id
    @Column(name = "userid")
    private int userId;

    @Id
    @Column(name = "date")
    private Timestamp recordTimestamp;

    @Column(name = "startingwalletbalance")
    private double startingWalletBalance;

    @Column(name = "startingoutstandingbalance")
    private double startingOutstandingBalance;

    @Column(name = "walletbalance")
    private double walletBalance;

    @Column(name = "outstandingbalance")
    private double outstandingBalance;

    @Column(name = "totalbalance")
    private double totalBalance;

    @Column(name = "lastupdated")
    private Timestamp lastUpdated;

    @Column(name = "createdby")
    private int createdBy;

    @Column(name = "walletid")
    private int walletId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", insertable = false, updatable = false)
    private UserDao user;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "walletid", insertable = false, updatable = false)
    private UserWalletDao wallet;


    public DailyLedgerDao() {
    }

    public DailyLedgerDao(int userId, Timestamp recordTimestamp, double startingWalletBalance, double startingOutstandingBalance, double walletBalance, double outstandingBalance, double totalBalance, Timestamp lastUpdated, int createdBy, int walletId) {
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

    public UserDao getUser() {
        return user;
    }

    public void setUser(UserDao user) {
        this.user = user;
    }

    public UserWalletDao getWallet() {
        return wallet;
    }

    public void setWallet(UserWalletDao wallet) {
        this.wallet = wallet;
    }



}
