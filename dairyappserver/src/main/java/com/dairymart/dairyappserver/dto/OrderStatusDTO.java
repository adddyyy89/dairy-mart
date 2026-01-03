package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.OrderStatusDao;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.sql.Date;

public class OrderStatusDTO {

    private int statusId;

    private String statusDesc;

    private int createdBy;

    private Date lastUpdated;

    public OrderStatusDTO() {
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public OrderStatusDTO(int statusId, String statusDesc, int createdBy, Date lastUpdated) {
        this.statusId = statusId;
        this.statusDesc = statusDesc;
        this.createdBy = createdBy;
        this.lastUpdated = lastUpdated;
    }

    public OrderStatusDTO(OrderStatusDao dao) {
        this.statusId = dao.getStatusId();
        this.statusDesc = dao.getStatusDesc();
        this.createdBy = dao.getCreatedBy();
        this.lastUpdated = dao.getLastUpdated();
    }
}
