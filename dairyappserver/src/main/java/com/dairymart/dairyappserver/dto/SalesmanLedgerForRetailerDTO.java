package com.dairymart.dairyappserver.dto;

import java.util.ArrayList;

public class SalesmanLedgerForRetailerDTO {

    private String retailerName;

    private String retailerAddress;

    private double balance;

    private ArrayList<LedgerTransactionsDTO> transactionsDTOS;

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getRetailerAddress() {
        return retailerAddress;
    }

    public void setRetailerAddress(String retailerAddress) {
        this.retailerAddress = retailerAddress;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<LedgerTransactionsDTO> getTransactionsDTOS() {
        return transactionsDTOS;
    }

    public void setTransactionsDTOS(ArrayList<LedgerTransactionsDTO> transactionsDTOS) {
        this.transactionsDTOS = transactionsDTOS;
    }
}
