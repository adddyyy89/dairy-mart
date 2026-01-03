package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.RetailOrderDao;
import com.dairymart.dairyappserver.dao.SalesmanOrdersDao;
import com.dairymart.dairyappserver.dao.SalesmanToRetailId;
import com.dairymart.dairyappserver.dao.UserDao;
import jakarta.persistence.*;

import java.sql.Timestamp;

public class SalesmanOrdersDTO {

    private int salesmanId;

    private int retailerOrderId;

    private Timestamp createdOn;

    private Timestamp lastUpdated;

    private Boolean isDelivered;

    private Boolean isAccepted;

    private Boolean isOnWay;

    private Timestamp deliveredTime;

    private Timestamp acceptedTime;


    private RetailOrderDTO order;

    private UserDTO salesman;

    public SalesmanOrdersDTO() {

    }

    public SalesmanOrdersDTO(int salesmanId, int retailerOrderId, Timestamp createdOn, Timestamp lastUpdated, Boolean isDelivered, Boolean isAccepted, Boolean isOnWay, Timestamp deliveredTime, Timestamp acceptedTime) {
        this.salesmanId = salesmanId;
        this.retailerOrderId = retailerOrderId;
        this.createdOn = createdOn;
        this.lastUpdated = lastUpdated;
        this.isDelivered = isDelivered;
        this.isAccepted = isAccepted;
        this.isOnWay = isOnWay;
        this.deliveredTime = deliveredTime;
        this.acceptedTime = acceptedTime;
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
        return acceptedTime;
    }

    public void setAcceptedTime(Timestamp acceptedTime) {
        this.acceptedTime = acceptedTime;
    }

    public RetailOrderDTO getOrder() {
        return order;
    }

    public void setOrder(RetailOrderDTO order) {
        this.order = order;
    }

    public UserDTO getSalesman() {
        return salesman;
    }

    public void setSalesman(UserDTO salesman) {
        this.salesman = salesman;
    }

    public SalesmanOrdersDTO(SalesmanOrdersDao dao) {
        this.acceptedTime = dao.getAcceptedTime();
        this.createdOn = dao.getCreatedOn();
        this.retailerOrderId = dao.getRetailerOrderId();
        this.deliveredTime = dao.getDeliveredTime();
        this.lastUpdated = dao.getLastUpdated();
        this.isAccepted = dao.getAccepted();
        this.isDelivered = dao.getDelivered();
        this.isOnWay = dao.getOnWay();
        if(dao.getOrder() != null)
            this.order = new RetailOrderDTO(dao.getOrder());
        if(dao.getSalesman() != null)
            this.salesman = new UserDTO(dao.getSalesman());
        this.salesmanId = dao.getSalesmanId();
    }
}
