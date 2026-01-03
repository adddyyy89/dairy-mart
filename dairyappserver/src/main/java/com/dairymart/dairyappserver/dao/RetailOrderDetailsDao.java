package com.dairymart.dairyappserver.dao;

import com.dairymart.dairyappserver.dto.RetailOrderDTO;
import com.dairymart.dairyappserver.dto.RetailOrderDetailsDTO;
import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "RetailOrderDetails", schema = "public")
@IdClass(RetailOrderDetailsId.class)
public class RetailOrderDetailsDao {

    @Id
    @Column(name = "orderid")
    private int orderId;

    @Id
    @Column(name = "productcode")
    private String productCode;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "lastupdated")
    private Date lastUpdated;

    @Column(name = "hsn")
    private String hsn;

    @Column(name = "unit")
    private String unit;

    @Column(name = "salerate")
    private String saleRate;

    @Column(name = "purchaserate")
    private String purchaseRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderid", updatable = false, insertable = false)
    private RetailOrderDao retailOrder;

    public RetailOrderDetailsDao() {
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

    public RetailOrderDao getRetailOrder() {
        return retailOrder;
    }

    public void setRetailOrder(RetailOrderDao retailOrder) {
        this.retailOrder = retailOrder;
    }



    public RetailOrderDetailsDao(RetailOrderDetailsDTO dto) {
        this.hsn = dto.getHsn();
        this.orderId = dto.getOrderId();
        this.lastUpdated = dto.getLastUpdated();
        this.productCode = dto.getProductCode();
        this.unit = dto.getUnit();
        this.purchaseRate = dto.getPurchaseRate();
        this.quantity = dto.getQuantity();
        this.saleRate = dto.getSaleRate();
        if(dto.getRetailOrder() != null) {
            this.retailOrder = new RetailOrderDao(dto.getRetailOrder());
        }

    }

}
