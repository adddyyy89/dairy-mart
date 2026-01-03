package com.dairymart.dairyappserver.dao;

import com.dairymart.dairyappserver.dto.OrderStatusDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Date;

@Entity
@Table(name="OrderStatus", schema = "public")
public class OrderStatusDao {

    @Id
    @Column(name = "statusid")
    private int statusId;

    @Column(name = "statusdesc")
    private String statusDesc;

    @Column(name = "createdby")
    private int createdBy;

    @Column(name = "lastupdated")
    private Date lastUpdated;

    public OrderStatusDao() {
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

    public OrderStatusDao(OrderStatusDTO dto) {
        this.statusDesc = dto.getStatusDesc();
        this.statusId = dto.getStatusId();
        this.createdBy = dto.getCreatedBy();
        this.lastUpdated = dto.getLastUpdated();
    }


}
