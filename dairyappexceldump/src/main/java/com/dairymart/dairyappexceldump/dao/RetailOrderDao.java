package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "RetailOrder", schema = "public")
public class RetailOrderDao {

    @Id
    @Column(name = "orderid")
    @SequenceGenerator(name = "RETAIL_ORDER_ID", sequenceName = "retail_order_seq", allocationSize = 1)
    @GeneratedValue(generator = "RETAIL_ORDER_ID", strategy = GenerationType.SEQUENCE)
    private int orderId;

    @Column(name = "orderdate")
    private Date orderDate;

    @Column(name = "retailerid")
    private int retailerId;

    @Column(name = "branchid")
    private int branchId;

    @Column(name = "createdby")
    private int createdBy;

    @Column(name = "createdon")
    private Date createdon;

    @Column(name = "lastupdated")
    private Date lastUpdated;

    @Column(name = "orderstatusid")
    private int orderStatusId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "retailerid", insertable = false, updatable = false)
    private ShopDao retailer;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "branchid", insertable = false, updatable = false)
    private BranchDao branch;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "orderstatusid", insertable = false, updatable = false)
    private OrderStatusDao status;

    @OneToMany(mappedBy = "retailOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
    private List<RetailOrderDetailsDao> orderDetails;

    public RetailOrderDao() {
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

    public ShopDao getRetailer() {
        return retailer;
    }

    public void setRetailer(ShopDao retailer) {
        this.retailer = retailer;
    }

    public BranchDao getBranch() {
        return branch;
    }

    public void setBranch(BranchDao branch) {
        this.branch = branch;
    }

    public OrderStatusDao getStatus() {
        return status;
    }

    public void setStatus(OrderStatusDao status) {
        this.status = status;
    }

    public List<RetailOrderDetailsDao> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<RetailOrderDetailsDao> orderDetails) {
        this.orderDetails = orderDetails;
    }

}
