package com.dairymart.dairyappserver.dto;

public class CrateHolderDTO {
    private int userId;
    private int userTypeId;
    private String name;
    private String phoneNumber;
    private int holding;
    private int sentToStores;
    private int atStore;
    private int returnedToBranch;
    private int returnedFromStore;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getHolding() {
        return holding;
    }

    public void setHolding(int holding) {
        this.holding = holding;
    }

    public int getSentToStores() {
        return sentToStores;
    }

    public void setSentToStores(int sentToStores) {
        this.sentToStores = sentToStores;
    }

    public int getAtStore() {
        return atStore;
    }

    public void setAtStore(int atStore) {
        this.atStore = atStore;
    }

    public int getReturnedToBranch() {
        return returnedToBranch;
    }

    public void setReturnedToBranch(int returnedToBranch) {
        this.returnedToBranch = returnedToBranch;
    }

    public int getReturnedFromStore() {
        return returnedFromStore;
    }

    public void setReturnedFromStore(int returnedFromStore) {
        this.returnedFromStore = returnedFromStore;
    }
}
