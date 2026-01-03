package com.dairymart.dairyappserver.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "brand", schema = "public")
public class BrandDao {

    @Id
    @Column(name = "brandid")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int brandId;

    @Column(name = "brandname")
    private String brandName;

    public BrandDao(int brandId, String brandName) {
        this.brandId = brandId;
        this.brandName = brandName;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public BrandDao() {
    }
}
