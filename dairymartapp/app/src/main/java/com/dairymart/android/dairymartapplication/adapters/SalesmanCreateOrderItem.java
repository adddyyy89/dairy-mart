package com.dairymart.android.dairymartapplication.adapters;

public class SalesmanCreateOrderItem {

    private int productId;

    private String productName;

    private String productCode;

    private int quantity;

    private String productImageUrl;

    private String productHsn;

    private String productPurchaseRate;

    private String productSaleRate;

    private String productMrp;

    public SalesmanCreateOrderItem(int productId, String productName, String productCode, String productImageUrl, String productHsn, String productPurchaseRate, String productSaleRate, String productMrp) {
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.productImageUrl = productImageUrl;
        this.productHsn = productHsn;
        this.productMrp = productMrp;
        this.productPurchaseRate = productPurchaseRate;
        this.productSaleRate = productSaleRate;
        this.quantity = 0;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductHsn() {
        return productHsn;
    }

    public void setProductHsn(String productHsn) {
        this.productHsn = productHsn;
    }

    public String getProductPurchaseRate() {
        return productPurchaseRate;
    }

    public void setProductPurchaseRate(String productPurchaseRate) {
        this.productPurchaseRate = productPurchaseRate;
    }

    public String getProductSaleRate() {
        return productSaleRate;
    }

    public void setProductSaleRate(String productSaleRate) {
        this.productSaleRate = productSaleRate;
    }

    public String getProductMrp() {
        return productMrp;
    }

    public void setProductMrp(String productMrp) {
        this.productMrp = productMrp;
    }
}
