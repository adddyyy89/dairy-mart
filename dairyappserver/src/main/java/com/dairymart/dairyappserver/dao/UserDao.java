package com.dairymart.dairyappserver.dao;

import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.util.DateUtil;
import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "User", schema = "public")

public class UserDao {

    @Id
    @Column(name = "userid")
    @SequenceGenerator(name = "USER_ID", sequenceName = "`user_seq`", allocationSize = 1)
    @GeneratedValue(generator = "USER_ID", strategy = GenerationType.SEQUENCE)
    private int userId;

    @Column(name = "phonenumber")
    private String phoneNumber;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "typeid")
    private int typeId;

    @Column(name = "emailid")
    private String emailId;

    @Column(name = "addressid")
    private int addressId;

    @Column(name = "createdby")
    private int createdBy;

    @Column(name = "createdon")
    private Date createdOn;

    @Column(name = "lastupdated")
    private Date lastUpdated;

    @Column(name = "password")
    private String password;

    @Column(name = "isactive")
    private Boolean isActive;

    @Column(name = "cratecount")
    private int crateCount;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "typeid", insertable = false, updatable = false)
    private UserTypeDao type;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "addressid", insertable = false, updatable = false)
    private UserAddressDao address;

    public UserDao() { }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int userTypeId) {
        this.typeId = userTypeId;
    }

    public String getEmailId() {
        return emailId;
    }

    public UserAddressDao getAddress() {
        return address;
    }

    public void setAddress(UserAddressDao address) {
        this.address = address;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserDao(UserDTO dto) {
        this.addressId = dto.getAddressId();
        this.createdBy = dto.getCreatedBy();
        this.createdOn = DateUtil.getDateFromString(dto.getCreatedOn());
        this.emailId = dto.getEmailId();
        //this.userId = dto.getUserId();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.lastUpdated = new Date(System.currentTimeMillis());
        this.password = dto.getPassword();
        this.phoneNumber = dto.getPhoneNumber();
        this.typeId = dto.getUserTypeId();
        this.isActive = dto.getActive();
        this.crateCount = dto.getCrateCount();
        if(dto.getAddress() != null) {
            this.setAddress(new UserAddressDao(dto.getAddress()));
        }
    }

    public UserTypeDao getType() {
        return type;
    }

    public void setType(UserTypeDao type) {
        this.type = type;
    }

    public int getCrateCount() {
        return crateCount;
    }

    public void setCrateCount(int crateCount) {
        this.crateCount = crateCount;
    }
}
