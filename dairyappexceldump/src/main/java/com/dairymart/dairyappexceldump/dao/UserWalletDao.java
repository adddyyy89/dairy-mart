package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "userwallet", schema = "public")
public class UserWalletDao {

    @Id
    @Column(name = "walletid")
    @SequenceGenerator(name = "WALLET_SEQ", sequenceName = "wallet_seq", allocationSize = 1)
    @GeneratedValue(generator = "WALLET_SEQ", strategy = GenerationType.SEQUENCE)
    private int walletId;

    @Column(name = "userid")
    private int userId;

    @Column(name = "balance")
    private double balance;

    @Column(name = "outstanding")
    private double outstanding;

    @Column(name = "createdon")
    private Date createdOn;

    @Column(name = "lastupdated")
    private Date lastUpdated;

    @Column(name = "createdby")
    private int createdBy;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", insertable = false, updatable = false)
    private UserDao user;


    public UserWalletDao() {
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

    public UserDao getUser() {
        return user;
    }

    public void setUser(UserDao owner) {
        this.user = owner;
    }

    public UserWalletDao(int userId, double balance, double outstanding, Date createdOn, Date lastUpdated, int createdBy, UserDao owner) {
        this.userId = userId;
        this.balance = balance;
        this.outstanding = outstanding;
        this.createdOn = createdOn;
        this.lastUpdated = lastUpdated;
        this.createdBy = createdBy;
        this.user = owner;
    }

}
