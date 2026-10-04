package com.dairymart.dairyappserver.dao;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "branch_inventory", schema = "public")
@IdClass(BranchInventoryId.class)
public class BranchInventoryDao {

    @Id
    @Column(name = "branchid")
    private int branchId;

    @Id
    @Column(name = "productid")
    private int productId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "lastupdated")
    private Timestamp lastUpdated;

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
