package com.dairymart.dairyappserver.dao;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "crate_pool", schema = "public")
public class CratePoolDao {

    @Id
    @Column(name = "poolid")
    private int poolId;

    @Column(name = "totalcrates")
    private int totalCrates;

    @Column(name = "availableatbranch")
    private int availableAtBranch;

    @Column(name = "lastupdated")
    private Timestamp lastUpdated;

    public int getPoolId() {
        return poolId;
    }

    public void setPoolId(int poolId) {
        this.poolId = poolId;
    }

    public int getTotalCrates() {
        return totalCrates;
    }

    public void setTotalCrates(int totalCrates) {
        this.totalCrates = totalCrates;
    }

    public int getAvailableAtBranch() {
        return availableAtBranch;
    }

    public void setAvailableAtBranch(int availableAtBranch) {
        this.availableAtBranch = availableAtBranch;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
