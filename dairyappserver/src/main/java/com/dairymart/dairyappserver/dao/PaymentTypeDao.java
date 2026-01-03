package com.dairymart.dairyappserver.dao;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "paymenttype", schema = "public")
public class PaymentTypeDao {

    @Id
    @Column(name = "paymenttypeid")
    private int productTypeId;

    @Column(name = "paymenttypename")
    private String productTypeName;

    @Column(name = "isactive")
    private boolean active;

    @Column(name = "createdon")
    private Timestamp createdOn;

    @Column(name = "createdby")
    private int createdBy;

    public int getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(int productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
}
