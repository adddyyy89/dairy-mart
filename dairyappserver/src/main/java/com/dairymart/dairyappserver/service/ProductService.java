package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.ProductDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.ProductDTO;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.repository.ProductRepository;
import com.dairymart.dairyappserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDao> getAllProducts() {
        return productRepository.findAll();
    }

    public ProductDao createProduct(ProductDao product) {
        return productRepository.save(product);
    }

    public ProductDao findById(int id) {
        Optional<ProductDao> productDao = productRepository.findById(id);
        return productDao.orElse(null);
    }

    public List<ProductDao> findProductByName(String productQuery) {
        //String pNumber = String.valueOf(phoneNumber);
        return productRepository.findAll().stream().filter(x -> x.getProductName().contains(productQuery)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<ProductDao> findByTypeId(int typeId) {
        return productRepository.findAll().stream().filter(x -> x.getProductTypeId() == typeId).collect(Collectors.toCollection(ArrayList::new));
    }

    public ProductDao updateById(ProductDTO dto) {

        int pId = dto.getProductId();
        ProductDao d = findById(pId);
        if(d == null) {
            return null;
        }

        ProductDao dao = new ProductDao(dto);
        dao.setProductId(dto.getProductId());
        dao.setLastUpdated(new Date(System.currentTimeMillis()));
        return productRepository.save(dao);


    }
}
