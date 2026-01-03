package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "product", schema = "public")
public class ProductDao {

    @Id
    @Column(name = "productid")
    @SequenceGenerator(name = "PRODUCT_ID", sequenceName = "product_seq", allocationSize = 1)
    @GeneratedValue(generator = "PRODUCT_ID", strategy = GenerationType.SEQUENCE)
    private int productId;

    @Column(name = "hsn")
    private String hsn;

    @Column(name = "productname")
    private String productName;

    @Column(name = "productshortname")
    private String productShortName;

    @Column(name = "productpurchaserate")
    private String productPurchaseRate;

    @Column(name = "productpictureurl")
    private String productPictureUrl;

    @Column(name = "createdby")
    private int createdBy;

    @Column(name = "createdon")
    private Date createdon;

    @Column(name = "lastupdated")
    private Date lastUpdated;

    @Column(name = "isactive")
    private Boolean isActive;

    @Column(name = "producttypeid")
    private int productTypeId;

    @Column(name = "brandid")
    private int brandId;

    @Column(name = "productsalerate")
    private String productSaleRate;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
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

    public String getIgst() {
        return igst;
    }

    public void setIgst(String igst) {
        this.igst = igst;
    }

    @Column(name = "productcode")
    private String productCode;

    @Column(name = "mrp")
    private String mrp;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "unit")
    private String unit;

    @Column(name = "igst")
    private String igst;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "producttypeid", insertable = false, updatable = false)
    private ProductTypeDao type;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "brandid", insertable = false, updatable = false)
    private BrandDao brand;

    public ProductDao(int productId, String hsn, String productName, String productShortName, String productPurchaseRate, String productPictureUrl, int createdBy, Date createdon, Date lastUpdated, Boolean isActive, int productTypeId, int brandId, String productSaleRate, String mrp, String productCode, String quantity, String unit, String igst) {
        this.productId = productId;
        this.hsn = hsn;
        this.productName = productName;
        this.productShortName = productShortName;
        this.productPurchaseRate = productPurchaseRate;
        this.productPictureUrl = productPictureUrl;
        this.createdBy = createdBy;
        this.createdon = createdon;
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
        return createdon;
    }

    public void setCreatedon(Date createdon) {
        this.createdon = createdon;
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

    public ProductDao() {
    }
}
