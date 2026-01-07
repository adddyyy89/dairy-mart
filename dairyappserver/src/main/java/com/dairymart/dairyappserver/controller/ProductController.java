package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.ProductDao;
import com.dairymart.dairyappserver.dao.ProductTypeDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.ProductDTO;
import com.dairymart.dairyappserver.dto.ProductTypeDTO;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.service.ProductService;
import com.dairymart.dairyappserver.service.ProductTypeService;
import com.dairymart.dairyappserver.service.UserService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    Logger logger = LoggerFactory.getLogger(ProductController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductTypeService productTypeService;

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addProduct(@RequestBody ProductDTO productDto) {
        logger.info("add product called.");
        if(productDto == null || productDto.getProductName().isEmpty()) {
            logger.error("Product details are missing. Unable to create new product");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Details missing.");
        }

        ProductDao productDao = productService.createProduct(new ProductDao(productDto));
        if(productDao != null) {
            logger.info("Product is added successfully.");
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new ProductDTO(productDao)));
        }

        logger.error("Unable to create product.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to create product.");

    }

    @CrossOrigin("*")
    @GetMapping(value = "/get/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUser(@PathVariable String productId) {
        logger.info("getProduct called with productId: {}", productId);

        if(productId == null || productId.isEmpty()) {
            logger.error("Product Id is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Id is missing.");
        }
        ProductDTO productDTO = null;
        try{
            int id = Integer.parseInt(productId);
            ProductDao dao = productService.findById(id);
            if(dao != null) {
                productDTO = new ProductDTO(dao);
            }
        } catch(NumberFormatException ex) {
            logger.error("Invalid product id format.", ex);
        }

        if(productDTO == null) {
            logger.info("Product details not found for productId = {}", productId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to get the product using id = " + productId);
        }

        logger.info("Product details fetched. {}", productDTO);
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(productDTO));
    }

    @GetMapping(value = "/get/producttype/{productTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUsersByType(@PathVariable String productTypeId) {
        logger.info("getProductsByType called with productTypeId: {}", productTypeId);

        if(productTypeId == null || productTypeId.isEmpty()) {
            logger.error("Product Type Id is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("product Type is missing");
        }
        List<ProductDTO> dtoList = new ArrayList<>();
        try{
            int id = Integer.parseInt(productTypeId);
            List<ProductDao> daoList = productService.findByTypeId(id);
            if(daoList != null && !daoList.isEmpty()) {
                for(ProductDao dao : daoList) {
                    dtoList.add(new ProductDTO(dao));
                }
            }
        } catch(NumberFormatException ex) {
            logger.error("Invalid product id format.", ex);
        }

        if(dtoList.isEmpty()) {
            logger.info("Product details not found for productTypeId = {}", productTypeId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Product details not found for productTypeId =" + productTypeId);
        }

        logger.info("Product details fetched of size = {}", dtoList.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(dtoList));
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateProduct(@RequestBody ProductDTO productDTO) {
        logger.info("update product called: {}", productDTO.getProductId());

        if(productDTO.getProductTypeId() <= 0) {
            logger.error("Product details is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product details is missing");
        }

        ProductDao dao1 = productService.updateById(productDTO);
        if(dao1 == null) {
            logger.info("Invalid product id provided. Product could not be found/");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product details not found.");
        }
        ProductDTO d = new ProductDTO(dao1);

        logger.info("Product is updated");
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(d));

    }

    @CrossOrigin("*")
    @GetMapping(value = "/getall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllProducts() {
        logger.info("get all products called");

        List<ProductDao> productDaoList = productService.getAllProducts();
        List<ProductDTO> dtoList = new ArrayList<>(productDaoList.size());
        for(ProductDao productDao : productDaoList) {
            dtoList.add(new ProductDTO(productDao));
        }

        if(dtoList.isEmpty()) {
            logger.info("No Products found");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Product not found");
        }

        logger.info("Product retrieved = {}", dtoList.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(dtoList));
    }


    @CrossOrigin("*")
    @GetMapping(value = "/producttype/getall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllProductTypes() {
        logger.info("get all product types called");

        List<ProductTypeDao> productTypeDaos = productTypeService.getAllProductTypes();
        List<ProductTypeDTO> dtoList = new ArrayList<>(productTypeDaos.size());
        for(ProductTypeDao productDao : productTypeDaos) {
            dtoList.add(new ProductTypeDTO(productDao));
        }

        if(dtoList.isEmpty()) {
            logger.info("No Products Type found");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Product Type not found");
        }

        logger.info("Product Types retrieved = {}", dtoList.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(dtoList));
    }



}
