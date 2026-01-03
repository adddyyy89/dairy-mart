package com.dairymart.android.dairymartapplication.adapters;

public class SalesmanDashboardRecentTransaction {

    private long transactionId;

    private long ledgerId;

    private String amount;

    private String transactionType;

    private String retailerName;

    private String transactionTimestamp;

    public SalesmanDashboardRecentTransaction(long transactionId, long ledgerId, String amount, String transactionType, String retailerName, String transactionTimestamp) {
        this.transactionId = transactionId;
        this.ledgerId = ledgerId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.retailerName = retailerName;
        this.transactionTimestamp = transactionTimestamp;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public long getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(long ledgerId) {
        this.ledgerId = ledgerId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public void setTransactionTimestamp(String transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }
}
