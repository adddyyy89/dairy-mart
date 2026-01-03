package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.CrateDao;

import java.sql.Timestamp;

public class CrateDTO {
    private int userId;
    private int crateCount;
    private int userTypeId;
    private Timestamp recordTimestamp;
    private int crateReceived;
    private int crateReturned;

    private UserDTO user;
    private UserTypeDTO type;

    public CrateDTO() {
    }

    public CrateDTO(int userId, int crateCount, int userTypeId, Timestamp recordTimestamp, int crateReceived, int crateReturned) {
        this.userId = userId;
        this.crateCount = crateCount;
        this.userTypeId = userTypeId;
        this.recordTimestamp = recordTimestamp;
        this.crateReceived = crateReceived;
        this.crateReturned = crateReturned;
    }

    public CrateDTO(CrateDao dao) {
        this.crateCount = dao.getCrateCount();
        this.crateReceived = dao.getCrateReceived();
        this.crateReturned = dao.getCrateReturned();
        this.recordTimestamp = dao.getRecordTimestamp();
        this.userId = dao.getUserId();
        this.userTypeId = dao.getUserTypeId();
        if(dao.getType() != null) {
            this.type = new UserTypeDTO(dao.getType());
        }
        if(dao.getUser() != null) {
            this.user = new UserDTO(dao.getUser());
        }
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCrateCount() {
        return crateCount;
    }

    public void setCrateCount(int crateCount) {
        this.crateCount = crateCount;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }

    public Timestamp getRecordTimestamp() {
        return recordTimestamp;
    }

    public void setRecordTimestamp(Timestamp recordTimestamp) {
        this.recordTimestamp = recordTimestamp;
    }

    public int getCrateReceived() {
        return crateReceived;
    }

    public void setCrateReceived(int crateReceived) {
        this.crateReceived = crateReceived;
    }

    public int getCrateReturned() {
        return crateReturned;
    }

    public void setCrateReturned(int crateReturned) {
        this.crateReturned = crateReturned;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public UserTypeDTO getType() {
        return type;
    }

    public void setType(UserTypeDTO type) {
        this.type = type;
    }
}
