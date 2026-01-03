package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.CityDao;
import com.dairymart.dairyappserver.dao.CrateDao;
import com.dairymart.dairyappserver.dao.StateDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.CityDTO;
import com.dairymart.dairyappserver.dto.CrateDTO;
import com.dairymart.dairyappserver.dto.StateDTO;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.service.CityService;
import com.dairymart.dairyappserver.service.CrateService;
import com.dairymart.dairyappserver.service.StateService;
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
@RequestMapping("/crate")
public class CrateController {

    Logger logger = LoggerFactory.getLogger(CrateController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private CrateService crateService;

    @GetMapping(value = "/get/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllCrates() {
        logger.info("Get all crate service called.");

        List<CrateDTO> crateDTOS = new ArrayList<>();
        List<CrateDao> crateDaos = crateService.getTotalCrates();
        for(CrateDao crate : crateDaos) {
            crateDTOS.add(new CrateDTO(crate));
        }

        logger.info("Get all crates service completed, Total crates : {}", crateDTOS.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(crateDTOS));
    }

    @GetMapping(value = "/get/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCrateDetailsByUserId(@PathVariable String userId) {
        logger.info("Get crate details for user id: {}", userId);

        // Get user id from the request
        int user = 0;
        try {
            user = Integer.parseInt(userId);
        }catch (NumberFormatException e){
            logger.error("Unable to parse the userId in request!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson("Invalid user id provided."));
        }

        List<CrateDao> crateDaos = crateService.getTotalCratesForUser(user);
        List<CrateDTO> crateDTOS = new ArrayList<>(crateDaos.size());

        for(CrateDao crate : crateDaos) {
            crateDTOS.add(new CrateDTO(crate));
        }

        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(crateDTOS));
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCrate(@RequestBody CrateDTO crateDTO) {
        logger.info("update crate called.");

        if(crateDTO == null || crateDTO.getUserId() <=0) {
            logger.error("Users details are missing. Unable to update crate details");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Details missing.");
        }

        CrateDao crateDao = crateService.updateCrate(new CrateDao(crateDTO));
        if(crateDao != null) {
            logger.info("Crate details is updated successfully.");
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new CrateDTO(crateDao)));
        }

        logger.error("Unable to update crate details.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to update crate details.");

    }

    @GetMapping(value = "/assigned/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAssignedCrates(@PathVariable String userId) {
        logger.info("Get assigned crate for user {} called.", userId);

        int id = -1;
        try{
            id = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            logger.error("Unable to parse user id.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson("Invalid user id provided"));
        }
        int cratesAssigned = crateService.getCurrentAssignedCrateForUser(id);


        logger.info("Get assigned crate for user {} completed. Total assigned crate = {}", id, cratesAssigned);
        return ResponseEntity.status(HttpStatus.OK).body(cratesAssigned + "");
    }

}
