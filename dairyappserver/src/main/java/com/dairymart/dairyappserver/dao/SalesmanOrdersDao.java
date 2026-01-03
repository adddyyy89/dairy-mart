package com.dairymart.dairyappserver.dao;

import com.dairymart.dairyappserver.dto.SalesmanOrdersDTO;
import com.dairymart.dairyappserver.dto.SalesmanToRetailDTO;
import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "salesmanorders", schema = "public")
@IdClass(SalesmanOrdersId.class)
public class SalesmanOrdersDao {

    @Id
    @Column(name = "salesmanid")
    private int salesmanId;

    @Id
    @Column(name = "retailerorderid")
    private int retailerOrderId;

    @Column(name = "createdon")
    private Timestamp createdOn;

    @Column(name = "lastupdated")
    private Timestamp lastUpdated;

    @Column(name = "isdelivered")
    private Boolean isDelivered;

    @Column(name = "isaccepted")
    private Boolean isAccepted;

    @Column(name = "isonway")
    private Boolean isOnWay;

    @Column(name = "deliveredtime")
    private Timestamp deliveredTime;

    @Column(name = "accepttime")
    private Timestamp acceptTime;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "retailerorderid", insertable = false, updatable = false)
    private RetailOrderDao order;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "salesmanid", insertable = false, updatable = false)
    private UserDao salesman;

    public SalesmanOrdersDao() {

    }

    public SalesmanOrdersDao(int salesmanId, int retailerOrderId, Timestamp createdOn, Timestamp lastUpdated, Boolean isDelivered, Boolean isAccepted, Boolean isOnWay, Timestamp deliveredTime, Timestamp acceptedTime) {
        this.salesmanId = salesmanId;
        this.retailerOrderId = retailerOrderId;
        this.createdOn = createdOn;
        this.lastUpdated = lastUpdated;
        this.isDelivered = isDelivered;
        this.isAccepted = isAccepted;
        this.isOnWay = isOnWay;
        this.deliveredTime = deliveredTime;
        this.acceptTime = acceptedTime;
    }

    public SalesmanOrdersDao(SalesmanOrdersDTO dto) {
        this.salesmanId = dto.getSalesmanId();
        this.retailerOrderId = dto.getRetailerOrderId();
        this.createdOn = dto.getCreatedOn();
        this.lastUpdated = dto.getLastUpdated();
        this.isDelivered = dto.getDelivered();
        this.isAccepted = dto.getAccepted();
        this.isOnWay = dto.getOnWay();
        this.deliveredTime = dto.getDeliveredTime();
        this.acceptTime = dto.getAcceptedTime();
    }

    public int getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(int salesmanId) {
        this.salesmanId = salesmanId;
    }

    public int getRetailerOrderId() {
        return retailerOrderId;
    }

    public void setRetailerOrderId(int retailerOrderId) {
        this.retailerOrderId = retailerOrderId;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getDelivered() {
        return isDelivered;
    }

    public void setDelivered(Boolean delivered) {
        isDelivered = delivered;
    }

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    public Boolean getOnWay() {
        return isOnWay;
    }

    public void setOnWay(Boolean onWay) {
        isOnWay = onWay;
    }

    public Timestamp getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(Timestamp deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public Timestamp getAcceptedTime() {
        return acceptTime;
    }

    public void setAcceptedTime(Timestamp acceptedTime) {
        this.acceptTime = acceptedTime;
    }

    public RetailOrderDao getOrder() {
        return order;
    }

    public void setOrder(RetailOrderDao order) {
        this.order = order;
    }

    public UserDao getSalesman() {
        return salesman;
    }

    public void setSalesman(UserDao salesman) {
        this.salesman = salesman;
    }
}
