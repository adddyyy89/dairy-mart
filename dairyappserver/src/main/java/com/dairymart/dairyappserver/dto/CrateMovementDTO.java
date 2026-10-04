package com.dairymart.dairyappserver.dto;

public class CrateMovementDTO {
    private int salesmanId;
    private int retailerUserId;
    private int quantity;

    public int getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(int salesmanId) {
        this.salesmanId = salesmanId;
    }

    public int getRetailerUserId() {
        return retailerUserId;
    }

    public void setRetailerUserId(int retailerUserId) {
        this.retailerUserId = retailerUserId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
