package com.dairymart.dairyappserver.dao;

import java.io.Serializable;
import java.util.Objects;

public class BranchInventoryId implements Serializable {
    private int branchId;
    private int productId;

    public BranchInventoryId() {
    }

    public BranchInventoryId(int branchId, int productId) {
        this.branchId = branchId;
        this.productId = productId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BranchInventoryId that)) {
            return false;
        }
        return branchId == that.branchId && productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(branchId, productId);
    }
}
