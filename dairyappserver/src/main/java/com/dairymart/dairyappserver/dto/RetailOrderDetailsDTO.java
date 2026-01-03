package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.RetailOrderDao;
import com.dairymart.dairyappserver.dao.RetailOrderDetailsDao;

import java.sql.Date;

public class RetailOrderDetailsDTO {


    private int orderId;

    private String productCode;

    private String quantity;

    private Date lastUpdated;

    private String hsn;

    private String unit;

    private String saleRate;

    private String purchaseRate;

    private RetailOrderDTO retailOrder;

    public RetailOrderDTO getRetailOrder() {
        return retailOrder;
    }

    public void setRetailOrder(RetailOrderDTO retailOrder) {
        this.retailOrder = retailOrder;
    }

    public RetailOrderDetailsDTO() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getHsn() {
        return hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSaleRate() {
        return saleRate;
    }

    public void setSaleRate(String saleRate) {
        this.saleRate = saleRate;
    }

    public String getPurchaseRate() {
        return purchaseRate;
    }

    public void setPurchaseRate(String purchaseRate) {
        this.purchaseRate = purchaseRate;
    }

    public RetailOrderDetailsDTO(RetailOrderDetailsDao dao) {
        this.hsn = dao.getHsn();
        this.orderId = dao.getOrderId();
        this.lastUpdated = dao.getLastUpdated();
        this.productCode = dao.getProductCode();
        this.unit = dao.getUnit();
        this.purchaseRate = dao.getPurchaseRate();
        this.quantity = dao.getQuantity();
        this.saleRate = dao.getSaleRate();
    }

}
