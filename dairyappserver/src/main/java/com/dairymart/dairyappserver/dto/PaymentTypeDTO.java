package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.PaymentTypeDao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

public class PaymentTypeDTO {

    private int productTypeId;

    private String productTypeName;

    private boolean active;

    private Timestamp createdOn;

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

    public PaymentTypeDTO(PaymentTypeDao dao) {
        this.active = dao.isActive();
        this.createdBy = dao.getCreatedBy();
        this.createdOn = dao.getCreatedOn();
        this.productTypeId = dao.getProductTypeId();
        this.productTypeName = dao.getProductTypeName();

    }
}
