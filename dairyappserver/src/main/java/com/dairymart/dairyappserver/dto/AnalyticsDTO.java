package com.dairymart.dairyappserver.dto;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsDTO {
    private int totalOrders;
    private long totalUnits;
    private double totalCollected;
    private int cratesInSystem;
    private List<AnalyticsRowDTO> topProducts = new ArrayList<>();
    private List<AnalyticsRowDTO> storesByOrders = new ArrayList<>();
    private List<AnalyticsRowDTO> salesmenCollected = new ArrayList<>();
    private List<AnalyticsRowDTO> storesPaid = new ArrayList<>();
    private List<AnalyticsRowDTO> storeProducts = new ArrayList<>();
    private List<AnalyticsRowDTO> crateLocations = new ArrayList<>();

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(long totalUnits) {
        this.totalUnits = totalUnits;
    }

    public double getTotalCollected() {
        return totalCollected;
    }

    public void setTotalCollected(double totalCollected) {
        this.totalCollected = totalCollected;
    }

    public int getCratesInSystem() {
        return cratesInSystem;
    }

    public void setCratesInSystem(int cratesInSystem) {
        this.cratesInSystem = cratesInSystem;
    }

    public List<AnalyticsRowDTO> getTopProducts() {
        return topProducts;
    }

    public void setTopProducts(List<AnalyticsRowDTO> topProducts) {
        this.topProducts = topProducts;
    }

    public List<AnalyticsRowDTO> getStoresByOrders() {
        return storesByOrders;
    }

    public void setStoresByOrders(List<AnalyticsRowDTO> storesByOrders) {
        this.storesByOrders = storesByOrders;
    }

    public List<AnalyticsRowDTO> getSalesmenCollected() {
        return salesmenCollected;
    }

    public void setSalesmenCollected(List<AnalyticsRowDTO> salesmenCollected) {
        this.salesmenCollected = salesmenCollected;
    }

    public List<AnalyticsRowDTO> getStoresPaid() {
        return storesPaid;
    }

    public void setStoresPaid(List<AnalyticsRowDTO> storesPaid) {
        this.storesPaid = storesPaid;
    }

    public List<AnalyticsRowDTO> getStoreProducts() {
        return storeProducts;
    }

    public void setStoreProducts(List<AnalyticsRowDTO> storeProducts) {
        this.storeProducts = storeProducts;
    }

    public List<AnalyticsRowDTO> getCrateLocations() {
        return crateLocations;
    }

    public void setCrateLocations(List<AnalyticsRowDTO> crateLocations) {
        this.crateLocations = crateLocations;
    }
}
