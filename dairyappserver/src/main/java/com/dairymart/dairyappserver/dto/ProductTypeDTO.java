package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.ProductTypeDao;

public class ProductTypeDTO {

    private int productTypeId;

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

    public ProductTypeDTO(int productTypeId, String productTypeName) {
        this.productTypeId = productTypeId;
        this.productTypeName = productTypeName;
    }

    public ProductTypeDTO(ProductTypeDao dao) {
        this.productTypeId = dao.getProductTypeId();
        this.productTypeName = dao.getProductTypeName();
    }
}
