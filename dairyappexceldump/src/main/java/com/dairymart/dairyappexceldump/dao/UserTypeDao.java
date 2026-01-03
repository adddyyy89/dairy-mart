package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name="UserType", schema = "public")
public class UserTypeDao {

    @Id
    @Column(name = "usertypeid")
    @SequenceGenerator(name = "USERTYPE_ID", sequenceName = "`usertype_seq`", allocationSize = 1)
    @GeneratedValue(generator = "USERTYPE_ID", strategy = GenerationType.SEQUENCE)
    private int userTypeId;

    @Column(name = "usertypedesc")
    private String userTypeDesc;

    @Column(name = "isactive")
    private Boolean isActive;

    @Column(name = "createdon")
    private Date createOn;

    @Column(name = "createdby")
    private int createBy;

    public UserTypeDao(String userTypeDesc, Boolean isActive, Date createOn, int createBy, int userTypeId) {
        this.userTypeDesc = userTypeDesc;
        this.isActive = isActive;
        this.createOn = createOn;
        this.createBy = createBy;
        this.userTypeId = userTypeId;
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

    public UserTypeDao() {

    }
}
