package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dao.UserTypeDao;
import jakarta.persistence.Column;

import java.sql.Date;

public class UserTypeDTO {
    private int userTypeId;
    private String userTypeDesc;
    private Boolean isActive;
    private Date createOn;
    private int createBy;

    public UserTypeDTO(int userTypeId, String userTypeDesc, Boolean isActive, Date createOn, int createBy) {
        this.userTypeId = userTypeId;
        this.userTypeDesc = userTypeDesc;
        this.isActive = isActive;
        this.createOn = createOn;
        this.createBy = createBy;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getUserTypeDesc() {
        return userTypeDesc;
    }

    public void setUserTypeDesc(String userTypeDesc) {
        this.userTypeDesc = userTypeDesc;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Date getCreateOn() {
        return createOn;
    }

    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }

    public int getCreateBy() {
        return createBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public UserTypeDTO (UserTypeDao dao) {
        this.userTypeId = dao.getUserTypeId();
        this.userTypeDesc = dao.getUserTypeDesc();
        this.isActive = dao.getActive();
        this.createOn = dao.getCreateOn();
        this.createBy = dao.getCreateBy();
    }
}
