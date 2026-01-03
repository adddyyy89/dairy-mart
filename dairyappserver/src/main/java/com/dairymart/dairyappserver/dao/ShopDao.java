package com.dairymart.dairyappserver.dao;

import com.dairymart.dairyappserver.dto.ShopDTO;
import jakarta.persistence.*;
import org.apache.catalina.User;

import java.sql.Date;

@Entity
@Table(name = "shop", schema = "public")
public class ShopDao {

    @Id
    @Column(name = "shopid")
    @SequenceGenerator(name = "SHOP_SEQ", sequenceName = "shop_seq", allocationSize = 1)
    @GeneratedValue(generator = "SHOP_SEQ", strategy = GenerationType.SEQUENCE)
    private int shopId;

    @Column(name = "shopname")
    private String shopName;

    @Column(name = "userid")
    private int userId;

    @Column(name = "addressid")
    private int addressId;

    @Column(name = "createdby")
    private int createdBy;

    @Column(name = "createdon")
    private Date createdOn;

    @Column(name = "lastupdated")
    private Date lastUpdated;

    @Column(name = "gstid")
    private int gstId;

    @Column(name = "isactive")
    private boolean isActive;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "gstid", insertable = false, updatable = false)
    private GSTDao gst;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", insertable = false, updatable = false)
    private UserDao owner;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "addressid", insertable = false, updatable = false)
    private UserAddressDao address;

    public ShopDao() {
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

    public ShopDao(int shopId, String shopName, int userId, int addressId, int createdBy, Date createdOn, Date lastUpdated, int gstId, boolean isActive) {
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

    public GSTDao getGst() {
        return gst;
    }

    public void setGst(GSTDao gst) {
        this.gst = gst;
    }

    public UserDao getOwner() {
        return owner;
    }

    public void setOwner(UserDao owner) {
        this.owner = owner;
    }

    public UserAddressDao getAddress() {
        return address;
    }

    public void setAddress(UserAddressDao address) {
        this.address = address;
    }

    public ShopDao(ShopDTO dto) {
        this.gstId = dto.getGstId();
        this.shopName = dto.getShopName();
        this.addressId = dto.getAddressId();
        this.createdBy = dto.getCreatedBy();
        this.createdOn = dto.getCreatedOn();
        this.isActive = dto.isActive();
        this.lastUpdated = dto.getLastUpdated();
        this.shopId = dto.getShopId();
        this.userId = dto.getUserId();
    }
}
