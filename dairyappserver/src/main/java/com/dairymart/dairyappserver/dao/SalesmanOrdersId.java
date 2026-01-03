package com.dairymart.dairyappserver.dao;

import java.sql.Timestamp;

public class SalesmanOrdersId {
    private int salesmanId;
    private int retailerOrderId;
    private Timestamp createdOn;

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
}
