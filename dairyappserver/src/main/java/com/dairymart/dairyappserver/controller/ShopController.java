package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.ProductDao;
import com.dairymart.dairyappserver.dao.ShopDao;
import com.dairymart.dairyappserver.dto.ProductDTO;
import com.dairymart.dairyappserver.dto.ShopDTO;
import com.dairymart.dairyappserver.service.ProductService;
import com.dairymart.dairyappserver.service.ShopService;
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
@RequestMapping("/shop")
public class ShopController {

    Logger logger = LoggerFactory.getLogger(ShopController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private ShopService shopService;

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addShop(@RequestBody ShopDTO shopDto) {
        logger.info("add shop called.");
        if(shopDto == null || shopDto.getShopName().isEmpty()) {
            logger.error("Shop details are missing. Unable to create new shop");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Shop Details missing.");
        }

        ShopDao shopDao = shopService.createShop(new ShopDao(shopDto));
        if(shopDao != null) {
            logger.info("Shop is added successfully.");
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new ShopDTO(shopDao)));
        }

        logger.error("Unable to create shop.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to create shop.");

    }

    @GetMapping(value = "/get/{shopId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getShop(@PathVariable String shopId) {
        logger.info("getShop called with productId: {}", shopId);

        if(shopId == null || shopId.isEmpty()) {
            logger.error("Shop Id is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Shop Id is missing.");
        }
        ShopDTO shopDTO = null;
        try{
            int id = Integer.parseInt(shopId);
            ShopDao dao = shopService.findById(id);
            if(dao != null) {
                shopDTO = new ShopDTO(dao);
            }
        } catch(NumberFormatException ex) {
            logger.error("Invalid shop id format.", ex);
        }

        if(shopDTO == null) {
            logger.info("Shop details not found for shopId = {}", shopId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to get the shop using id = " + shopId);
        }

        logger.info("Shop details fetched. {}", shopDTO);
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(shopDTO));
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateShop(@RequestBody ShopDTO shopDTO) {
        logger.info("update shop called: {}", shopDTO.getShopId());

        if(shopDTO.getShopId() <= 0) {
            logger.error("Shop details is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Shop details is missing");
        }

        ShopDao dao1 = shopService.updateById(shopDTO);
        if(dao1 == null) {
            logger.info("Invalid shop id provided. Shop could not be found/");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Shop details not found.");
        }
        ShopDTO d = new ShopDTO(dao1);

        logger.info("Shop is updated");
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(d));

    }

    @GetMapping(value = "/get/address/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getShopByAddress(@PathVariable String name) {
        logger.info("getShop called with query address: {}", name);

        if(name == null || name.isEmpty()) {
            logger.error("Query address  is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Query address is missing.");
        }
        List<ShopDTO> shopDTOList = new ArrayList<>();
        try{
            List<ShopDao> daoList = shopService.getAllShopsByAreaName(name);
            if(daoList != null && !daoList.isEmpty()) {
                daoList.forEach(d -> shopDTOList.add(new ShopDTO(d)));
            }
        } catch(NumberFormatException ex) {
            logger.error("Invalid shop id format.", ex);
        }

        if(shopDTOList == null || shopDTOList.isEmpty()) {
            logger.info("Shop details not found for query = {}", name);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to get the shop using query name = " + name);
        }

        logger.info("Shop details fetched. Total Shops = {}", shopDTOList.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(shopDTOList));
    }

    @GetMapping(value = "/get/user/{retailerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getShopByRetailerId(@PathVariable String retailerId) {
        logger.info("Get shops for retailer id : " + retailerId);
        int userId = Integer.parseInt(retailerId);
        List<ShopDTO> shopList = new ArrayList<>();

        List<ShopDao> daoList = shopService.getShopByRetailerId(userId);
        List<ShopDTO> dtos = new ArrayList<>();
        for(ShopDao shopDao : daoList) {
            dtos.add(new ShopDTO(shopDao));
        }

        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(dtos));
    }

}
