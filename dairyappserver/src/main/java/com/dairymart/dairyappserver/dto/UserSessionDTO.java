package com.dairymart.dairyappserver.dto;

import java.sql.Timestamp;

public class UserSessionDTO {
    private int userId;
    private String name;
    private String phoneNumber;
    private int role;
    private String roleLabel;
    private Timestamp loggedIn;
    private Timestamp loggedOut;
    private boolean active;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRoleLabel() {
        return roleLabel;
    }

    public void setRoleLabel(String roleLabel) {
        this.roleLabel = roleLabel;
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
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
