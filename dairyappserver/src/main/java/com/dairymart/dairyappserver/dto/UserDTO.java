package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dao.UserTypeDao;

public class UserDTO {
    private int userId;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private int userTypeId;
    private String emailId;
    private int addressId;
    private int createdBy;
    private String createdOn;
    private String lastUpdated;
    private int crateCount;

    private UserTypeDTO type;
    private UserAddressDTO address;

    public UserDTO(String phoneNumber, String firstName, String lastName, int userTypeId, String emailId, int addressId, String password, boolean isActive, int createCount) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userTypeId = userTypeId;
        this.emailId = emailId;
        this.addressId = addressId;
        this.password = password;
        this.isActive = isActive;
        this.crateCount = createCount;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    private Boolean isActive;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

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

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getEmailId() {
        return emailId;
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

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public UserDTO(UserDao dao) {
        this.addressId = dao.getAddressId();
        this.createdBy = dao.getCreatedBy();
        this.createdOn = dao.getCreatedOn().toString();
        this.emailId = dao.getEmailId();
        this.userId = dao.getUserId();
        this.firstName = dao.getFirstName();
        this.lastName = dao.getLastName();
        this.lastUpdated = dao.getLastUpdated().toString();
        this.password = dao.getPassword();
        this.phoneNumber = dao.getPhoneNumber();
        this.userTypeId = dao.getTypeId();
        this.isActive = dao.getActive();
        this.crateCount = dao.getCrateCount();
        if(dao.getType() != null)
            this.type = new UserTypeDTO(dao.getType());
        if(dao.getAddress() != null)
            this.address = new UserAddressDTO(dao.getAddress());
    }

    public UserTypeDTO getType() {
        return type;
    }

    public void setType(UserTypeDTO type) {
        this.type = type;
    }

    public UserAddressDTO getAddress() {
        return address;
    }

    public void setAddress(UserAddressDTO address) {
        this.address = address;
    }

    public int getCrateCount() {
        return crateCount;
    }

    public void setCrateCount(int crateCount) {
        this.crateCount = crateCount;
    }

    /*@Override
    public String toString() {
        return "UserId: " + userId + ", FirstName: " + firstName + ", LastName: " + lastName + ", UserTypeId: " + userTypeId;
    }*/
}
