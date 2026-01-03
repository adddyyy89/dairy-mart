package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.BrandDao;
import com.dairymart.dairyappserver.dao.ProductDao;
import com.dairymart.dairyappserver.dao.ProductTypeDao;
import jakarta.persistence.*;

import java.sql.Date;


public class ProductDTO {

    private int productId;


    private String hsn;


    private String productName;


    private String productShortName;


    private String productPurchaseRate;


    private String productPictureUrl;


    private int createdBy;


    private Date createdOn;


    private Date lastUpdated;


    private Boolean isActive;


    private int productTypeId;


    private int brandId;


    private String productSaleRate;

    private String igst;

    private String productCode;

    private String quantity;

    private String unit;

    private String mrp;

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getIgst() {
        return igst;
    }

    public void setIgst(String igst) {
        this.igst = igst;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    private ProductTypeDao type;


    private BrandDao brand;

    public ProductDTO(int productId, String hsn, String productName, String productShortName, String productPurchaseRate, String productPictureUrl, int createdBy, Date createdon, Date lastUpdated, Boolean isActive, int productTypeId, int brandId, String productSaleRate, String mrp, String productCode, String quantity, String unit, String igst ) {
        this.productId = productId;
        this.hsn = hsn;
        this.productName = productName;
        this.productShortName = productShortName;
        this.productPurchaseRate = productPurchaseRate;
        this.productPictureUrl = productPictureUrl;
        this.createdBy = createdBy;
        this.createdOn = createdon;
        this.lastUpdated = lastUpdated;
        this.isActive = isActive;
        this.productTypeId = productTypeId;
        this.brandId = brandId;
        this.productSaleRate = productSaleRate;
        this.mrp = mrp;
        this.productCode = productCode;
        this.quantity = quantity;
        this.unit = unit;
        this.igst = igst;
    }

    public ProductDTO(ProductDao dao) {
        this.setProductId(dao.getProductId());
        this.setActive(dao.getActive());
        this.setBrandId(dao.getBrandId());
        this.setHsn(dao.getHsn());
        this.setCreatedBy(dao.getCreatedBy());
        this.setCreatedon(dao.getCreatedon());
        this.setLastUpdated(dao.getLastUpdated());
        this.setProductName(dao.getProductName());
        this.setProductPictureUrl(dao.getProductPictureUrl());
        this.setProductPurchaseRate(dao.getProductPurchaseRate());
        this.setProductSaleRate(dao.getProductSaleRate());
        this.setProductShortName(dao.getProductShortName());
        this.setProductTypeId(dao.getProductTypeId());
        this.setBrand(dao.getBrand());
        this.setType(dao.getType());
        this.setMrp(dao.getMrp());
        this.setProductCode(dao.getProductCode());
        this.setQuantity(dao.getQuantity());
        this.setUnit(dao.getUnit());
        this.setIgst(dao.getIgst());
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getHsn() {
        return hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductShortName() {
        return productShortName;
    }

    public void setProductShortName(String productShortName) {
        this.productShortName = productShortName;
    }

    public String getProductPurchaseRate() {
        return productPurchaseRate;
    }

    public void setProductPurchaseRate(String productPurchaseRate) {
        this.productPurchaseRate = productPurchaseRate;
    }

    public String getProductPictureUrl() {
        return productPictureUrl;
    }

    public void setProductPictureUrl(String productPictureUrl) {
        this.productPictureUrl = productPictureUrl;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedon() {
        return createdOn;
    }

    public void setCreatedon(Date createdon) {
        this.createdOn = createdon;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public int getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(int productTypeId) {
        this.productTypeId = productTypeId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getProductSaleRate() {
        return productSaleRate;
    }

    public void setProductSaleRate(String productSaleRate) {
        this.productSaleRate = productSaleRate;
    }

    public ProductTypeDao getType() {
        return type;
    }

    public void setType(ProductTypeDao type) {
        this.type = type;
    }

    public BrandDao getBrand() {
        return brand;
    }

    public void setBrand(BrandDao brand) {
        this.brand = brand;
    }
}
