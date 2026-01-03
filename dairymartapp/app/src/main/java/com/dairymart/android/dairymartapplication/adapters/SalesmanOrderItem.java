package com.dairymart.android.dairymartapplication.adapters;

public class SalesmanOrderItem {

    private int retailerId;

    private String retailerName;

    private int orderId;

    private int orderStatusId;

    private String orderStatusMessage;

    private String orderTimestamp;

    private double orderAmount;

    public SalesmanOrderItem(int retailerId, String retailerName, int orderId, int orderStatusId, String orderStatusMessage, String orderTimestamp, double orderAmount) {
        this.retailerId = retailerId;
        this.retailerName = retailerName;
        this.orderStatusId = orderStatusId;
        this.orderStatusMessage = orderStatusMessage;
        this.orderTimestamp = orderTimestamp;
        this.orderAmount = orderAmount;
        this.orderId = orderId;
    }

    public SalesmanOrderItem() {
    }

    public int getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(int retailerId) {
        this.retailerId = retailerId;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public int getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(int orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public String getOrderStatusMessage() {
        return orderStatusMessage;
    }

    public void setOrderStatusMessage(String orderStatusMessage) {
        this.orderStatusMessage = orderStatusMessage;
    }

    public String getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(String orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
