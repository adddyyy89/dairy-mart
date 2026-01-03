package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "UserLogin", schema = "public")
@IdClass(UserLoginId.class)
public class UserLoginDao {

    @Id
    @Column(name = "userid")
    private int userId;

    @Column(name = "token")
    private String token;

    @Id
    @Column(name = "loggedin")
    private Timestamp loggedIn;

    @Column(name = "loggedout")
    private Timestamp loggedOut;

    @Column(name = "active")
    private boolean isActive;

    @Column(name = "phonenumber")
    private String phoneNumber;

    @Column(name = "role")
    private int role;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Timestamp loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Timestamp getLoggedOut() {
        return loggedOut;
    }

    public void setLoggedOut(Timestamp loggedOut) {
        this.loggedOut = loggedOut;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public UserLoginDao() {
    }
}
