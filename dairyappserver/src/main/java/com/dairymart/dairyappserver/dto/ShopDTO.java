package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.GSTDao;
import com.dairymart.dairyappserver.dao.ShopDao;
import com.dairymart.dairyappserver.dao.UserAddressDao;
import com.dairymart.dairyappserver.dao.UserDao;
import jakarta.persistence.*;
import org.apache.catalina.User;

import java.sql.Date;

public class ShopDTO {

    private int shopId;
    private String shopName;
    private int userId;
    private int addressId;
    private int createdBy;
    private Date createdOn;
    private Date lastUpdated;
    private int gstId;
    private boolean isActive;

    private GSTDTO gst;
    private UserDTO owner;
    private UserAddressDTO address;

    public ShopDTO() {
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getGstId() {
        return gstId;
    }

    public void setGstId(int gstId) {
        this.gstId = gstId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ShopDTO(int shopId, String shopName, int userId, int addressId, int createdBy, Date createdOn, Date lastUpdated, int gstId, boolean isActive) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.userId = userId;
        this.addressId = addressId;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.lastUpdated = lastUpdated;
        this.gstId = gstId;
        this.isActive = isActive;
    }

    public GSTDTO getGst() {
        return gst;
    }

    public void setGst(GSTDTO gst) {
        this.gst = gst;
    }

    public UserDTO getOwner() {
        return owner;
    }

    public void setOwner(UserDTO owner) {
        this.owner = owner;
    }

    public UserAddressDTO getAddress() {
        return address;
    }

    public void setAddress(UserAddressDTO address) {
        this.address = address;
    }

    public ShopDTO(ShopDao dao) {
        if(dao.getAddress() != null)
            this.setAddress(new UserAddressDTO(dao.getAddress()));
        this.setActive(dao.isActive());
        this.setAddressId(dao.getAddressId());
        if (dao.getGst() != null)
            this.setGst(new GSTDTO(dao.getGst()));
        this.setShopId(dao.getShopId());
        this.setShopName(dao.getShopName());
        this.setCreatedBy(dao.getCreatedBy());
        this.setCreatedOn(dao.getCreatedOn());
        this.setGstId(dao.getGstId());
        if(dao.getOwner() != null)
            this.setOwner(new UserDTO(dao.getOwner()));
        this.setUserId(dao.getUserId());
    }

}
