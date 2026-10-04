package com.dairymart.dairyappserver.dto;

import java.util.ArrayList;
import java.util.List;

public class CrateSummaryDTO {
    private int totalInSystem;
    private int atBranch;
    private int withSalesmen;
    private int atStores;
    private List<CrateHolderDTO> salesmen = new ArrayList<>();
    private List<CrateHolderDTO> stores = new ArrayList<>();

    public int getTotalInSystem() {
        return totalInSystem;
    }

    public void setTotalInSystem(int totalInSystem) {
        this.totalInSystem = totalInSystem;
    }

    public int getAtBranch() {
        return atBranch;
    }

    public void setAtBranch(int atBranch) {
        this.atBranch = atBranch;
    }

    public int getWithSalesmen() {
        return withSalesmen;
    }

    public void setWithSalesmen(int withSalesmen) {
        this.withSalesmen = withSalesmen;
    }

    public int getAtStores() {
        return atStores;
    }

    public void setAtStores(int atStores) {
        this.atStores = atStores;
    }

    public List<CrateHolderDTO> getSalesmen() {
        return salesmen;
    }

    public void setSalesmen(List<CrateHolderDTO> salesmen) {
        this.salesmen = salesmen;
    }

    public List<CrateHolderDTO> getStores() {
        return stores;
    }

    public void setStores(List<CrateHolderDTO> stores) {
        this.stores = stores;
    }
}
