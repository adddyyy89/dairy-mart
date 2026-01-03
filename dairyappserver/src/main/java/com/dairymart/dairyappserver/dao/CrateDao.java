package com.dairymart.dairyappserver.dao;

import com.dairymart.dairyappserver.dto.CrateDTO;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.dto.UserTypeDTO;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "crates", schema = "public")
@IdClass(CrateId.class)
public class CrateDao {

    @Id
    @Column(name = "userid")
    private int userId;

    @Column(name = "cratecount")
    private int crateCount;

    @Column(name = "usertypeid")
    private int userTypeId;

    @Id
    @Column(name = "recordtimestamp")
    private Timestamp recordTimestamp;

    @Column(name = "cratereceived")
    private int crateReceived;

    @Column(name = "cratereturned")
    private int crateReturned;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", insertable = false, updatable = false)
    private UserDao user;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "usertypeid", insertable = false, updatable = false)
    private UserTypeDao type;

    public CrateDao() {
    }

    public CrateDao(int userId, int crateCount, int userTypeId, Timestamp recordTimestamp, int crateReceived, int crateReturned) {
        this.userId = userId;
        this.crateCount = crateCount;
        this.userTypeId = userTypeId;
        this.recordTimestamp = recordTimestamp;
        this.crateReceived = crateReceived;
        this.crateReturned = crateReturned;
    }

    public CrateDao(int userId, int crateCount, int userTypeId, Timestamp recordTimestamp, int crateReceived, int crateReturned, UserDao user, UserTypeDao type) {
        this.userId = userId;
        this.crateCount = crateCount;
        this.userTypeId = userTypeId;
        this.recordTimestamp = recordTimestamp;
        this.crateReceived = crateReceived;
        this.crateReturned = crateReturned;
        this.user = user;
        this.type = type;
    }

    public CrateDao(CrateDTO dto) {
        this.crateCount = dto.getCrateCount();
        this.crateReceived = dto.getCrateReceived();
        this.crateReturned = dto.getCrateReturned();
        this.recordTimestamp = dto.getRecordTimestamp();
        this.userId = dto.getUserId();
        this.userTypeId = dto.getUserTypeId();
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

    public UserDao getUser() {
        return user;
    }

    public void setUser(UserDao user) {
        this.user = user;
    }

    public UserTypeDao getType() {
        return type;
    }

    public void setType(UserTypeDao type) {
        this.type = type;
    }
}
