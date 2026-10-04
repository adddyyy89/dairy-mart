package com.dairymart.android.dairymartapplication.adapters;

import java.util.Date;
import java.util.List;

public class SalesmanPendingOrder {

    private long orderId;

    private String orderDate;
    private int retailerId;

    private int branchId;

    private int createdBy;

    private String createdOn;

    private String lastUpdated;

    private int orderStatusId;

    private RetailerDetail retailerDetail;

    private OrderStatus orderStatus;

    private List<OrderDetail> orderDetails;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(int retailerId) {
        this.retailerId = retailerId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(int orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public RetailerDetail getRetailerDetail() {
        return retailerDetail;
    }

    public void setRetailerDetail(RetailerDetail retailerDetail) {
        this.retailerDetail = retailerDetail;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public static class RetailerDetail {

        public RetailerDetail(){}
        private int shopId;
        private String shopName;
        private int retailerId;

        private int gstId;

        private String gstNumber;

        private String aadharNumber;

        private String panNumber;

        private int userId;

        private String phoneNumber;

        private String firstName;

        private String lastName;

        private int crateCount;

        private String fullAddress;

        private String cityName;

        private String stateName;

        public int getShopId() {
            return shopId;
        }

        public void setShopId(int shopId) {
            this.shopId = shopId;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public int getRetailerId() {
            return retailerId;
        }

        public void setRetailerId(int retailerId) {
            this.retailerId = retailerId;
        }

        public int getGstId() {
            return gstId;
        }

        public void setGstId(int gstId) {
            this.gstId = gstId;
        }

        public String getGstNumber() {
            return gstNumber;
        }

        public void setGstNumber(String gstNumber) {
            this.gstNumber = gstNumber;
        }

        public String getAadharNumber() {
            return aadharNumber;
        }

        public void setAadharNumber(String aadharNumber) {
            this.aadharNumber = aadharNumber;
        }

        public String getPanNumber() {
            return panNumber;
        }

        public void setPanNumber(String panNumber) {
            this.panNumber = panNumber;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getCrateCount() {
            return crateCount;
        }

        public void setCrateCount(int crateCount) {
            this.crateCount = crateCount;
        }

        public String getFullAddress() {
            return fullAddress;
        }

        public void setFullAddress(String fullAddress) {
            this.fullAddress = fullAddress;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getStateName() {
            return stateName;
        }

        public void setStateName(String stateName) {
            this.stateName = stateName;
        }
    }

    public static class OrderStatus {

        public OrderStatus(){}
        private int statusId;

        private String statusDesc;

        private String lastUpdated;

        public int getStatusId() {
            return statusId;
        }

        public void setStatusId(int statusId) {
            this.statusId = statusId;
        }

        public String getStatusDesc() {
            return statusDesc;
        }

        public void setStatusDesc(String statusDesc) {
            this.statusDesc = statusDesc;
        }

        public String getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(String lastUpdated) {
            this.lastUpdated = lastUpdated;
        }
    }

    public static class OrderDetail {
        public OrderDetail(){}
        private int orderId;
        private String productCode;

        private String quantity;

        private String lastUpdated;

        private String hsn;

        private String unit;

        private String saleRate;

        private String purchaseRate;

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(String lastUpdated) {
            this.lastUpdated = lastUpdated;
        }

        public String getHsn() {
            return hsn;
        }

        public void setHsn(String hsn) {
            this.hsn = hsn;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getSaleRate() {
            return saleRate;
        }

        public void setSaleRate(String saleRate) {
            this.saleRate = saleRate;
        }

        public String getPurchaseRate() {
            return purchaseRate;
        }

        public void setPurchaseRate(String purchaseRate) {
            this.purchaseRate = purchaseRate;
        }
    }
}
