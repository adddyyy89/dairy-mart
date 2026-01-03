package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.UserLoginDao;

import java.sql.Timestamp;

public class UserLoginDTO {
    private String phoneNumber;
    private String password;
    private int userId;
    private String token;
    private Timestamp loggedIn;
    private Timestamp loggedOut;
    private int role;

    public UserLoginDTO(UserLoginDao dao) {
        setLoggedIn(dao.getLoggedIn());
        setIsActive(dao.isActive());
        setLoggedOut(dao.getLoggedOut());
        setPhoneNumber(dao.getPhoneNumber());
        setToken(dao.getToken());
        setUserId(dao.getUserId());
        setRole(dao.getRole());
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    private boolean isActive;


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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public UserLoginDTO() {
    }
}
