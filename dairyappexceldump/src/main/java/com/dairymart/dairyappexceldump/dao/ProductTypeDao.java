package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "producttype", schema = "public")
public class ProductTypeDao {

    @Id
    @Column(name = "producttypeid")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int productTypeId;

    @Column(name = "producttypename")
    private String productTypeName;

    public int getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(int productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public ProductTypeDao(int productTypeId, String productTypeName) {
        this.productTypeId = productTypeId;
        this.productTypeName = productTypeName;
    }

    public ProductTypeDao(){

    }
}
