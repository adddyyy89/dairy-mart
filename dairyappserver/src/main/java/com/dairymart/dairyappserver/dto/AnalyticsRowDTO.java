package com.dairymart.dairyappserver.dto;

public class AnalyticsRowDTO {
    private String name;
    private String detail;
    private long count;
    private double amount;

    public AnalyticsRowDTO() {
    }

    public AnalyticsRowDTO(String name, String detail, long count, double amount) {
        this.name = name;
        this.detail = detail;
        this.count = count;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
