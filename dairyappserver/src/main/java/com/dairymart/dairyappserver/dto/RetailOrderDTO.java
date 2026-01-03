package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.*;
import jakarta.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class RetailOrderDTO {

    private int orderId;

    private Date orderDate;

    private int retailerId;

    private int branchId;

    private int createdBy;

    private Date createdon;

    private Date lastUpdated;

    private int orderStatusId;

    private ShopDTO retailer;

    private BranchDTO branch;

    private OrderStatusDTO status;

    private List<RetailOrderDetailsDTO> orderDetails;

    public RetailOrderDTO() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public int getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(int retailerId) {
        this.retailerId = retailerId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedon() {
        return createdon;
    }

    public void setCreatedon(Date createdon) {
        this.createdon = createdon;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(int orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public ShopDTO getRetailer() {
        return retailer;
    }

    public void setRetailer(ShopDTO  retailer) {
        this.retailer = retailer;
    }

    public BranchDTO  getBranch() {
        return branch;
    }

    public void setBranch(BranchDTO  branch) {
        this.branch = branch;
    }

    public OrderStatusDTO  getStatus() {
        return status;
    }

    public void setStatus(OrderStatusDTO  status) {
        this.status = status;
    }

    public List<RetailOrderDetailsDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<RetailOrderDetailsDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public RetailOrderDTO(int orderId, Date orderDate, int retailerId, int branchId, int createdBy, Date createdon, Date lastUpdated, int orderStatusId, List<RetailOrderDetailsDTO> orderDetais) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.retailerId = retailerId;
        this.branchId = branchId;
        this.createdBy = createdBy;
        this.createdon = createdon;
        this.lastUpdated = lastUpdated;
        this.orderStatusId = orderStatusId;
        this.orderDetails = orderDetais;
    }

    public RetailOrderDTO(RetailOrderDao dao){
        this.createdBy = dao.getCreatedBy();
        this.orderDate = dao.getOrderDate();
        this.orderStatusId = dao.getOrderStatusId();
        this.retailerId = dao.getRetailerId();
        this.branchId = dao.getBranchId();
        this.createdon = dao.getCreatedon();
        this.lastUpdated = dao.getLastUpdated();
        this.orderId = dao.getOrderId();
        if(dao.getBranch() != null)
            this.branch = new BranchDTO(dao.getBranch());
        if(dao.getRetailer() != null)
            this.retailer = new ShopDTO(dao.getRetailer());
        if(dao.getStatus() != null)
            this.status = new OrderStatusDTO(dao.getStatus());
        if(dao.getOrderDetails() != null) {
            this.orderDetails = new ArrayList<>();
            for(RetailOrderDetailsDao d : dao.getOrderDetails()) {
                this.orderDetails.add(new RetailOrderDetailsDTO(d));
            }
        }
    }
}
