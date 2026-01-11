package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.ProductDao;
import com.dairymart.dairyappserver.dao.ProductTypeDao;
import com.dairymart.dairyappserver.dto.ProductDTO;
import com.dairymart.dairyappserver.repository.ProductRepository;
import com.dairymart.dairyappserver.repository.ProductTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductTypeService {

    @Autowired
    private ProductTypeRepository productTypeRepository;

    public List<ProductTypeDao> getAllProductTypes() {
        return productTypeRepository.findAll();
    }

    public ProductTypeDao createProductType(ProductTypeDao productTypeDao) {
        return productTypeRepository.save(productTypeDao);
    }

    public ProductTypeDao findById(int id) {
        Optional<ProductTypeDao> productDao = productTypeRepository.findById(id);
        return productDao.orElse(null);
    }
}
