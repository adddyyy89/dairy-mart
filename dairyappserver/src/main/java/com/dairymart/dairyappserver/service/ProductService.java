package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.ProductDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.ProductDTO;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.repository.ProductRepository;
import com.dairymart.dairyappserver.repository.ProductTypeRepository;
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
        product.setProductId(0);
        Date now = new Date(System.currentTimeMillis());
        if (product.getCreatedon() == null) {
            product.setCreatedon(now);
        }
        product.setLastUpdated(now);
        if (product.getActive() == null) {
            product.setActive(true);
        }
        if (product.getProductCode() == null || product.getProductCode().isBlank()) {
            String base = product.getProductName() == null ? "PROD" : product.getProductName().replaceAll("[^A-Za-z0-9]", "");
            if (base.length() > 8) {
                base = base.substring(0, 8);
            }
            if (base.isEmpty()) {
                base = "PROD";
            }
            product.setProductCode(base.toUpperCase() + "-" + (System.currentTimeMillis() % 100000));
        }
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
