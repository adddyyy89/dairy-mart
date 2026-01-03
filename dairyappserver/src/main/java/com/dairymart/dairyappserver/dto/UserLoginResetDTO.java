package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.UserLoginDao;

import java.sql.Timestamp;

public class UserLoginResetDTO {
    private String phoneNumber;
    private String newPassword;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public UserLoginResetDTO() {
    }
}
