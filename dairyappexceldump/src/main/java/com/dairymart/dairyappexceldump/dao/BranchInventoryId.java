package com.dairymart.dairyappexceldump.dao;

import java.io.Serializable;
import java.util.Objects;

public class BranchInventoryId implements Serializable {
    private int branchId;
    private int productId;

    public BranchInventoryId() {
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
