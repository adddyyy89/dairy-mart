package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dao.UserTypeDao;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.dto.UserTypeDTO;
import com.dairymart.dairyappserver.service.UserService;
import com.dairymart.dairyappserver.service.UserTypeService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/usertype")
public class UserTypeController {

    Logger logger = LoggerFactory.getLogger(UserTypeController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private UserTypeService userTypeService;

    @GetMapping(value = "/get/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllUserTypes() {
        logger.info("get all user types called.");
        List<UserTypeDao> daoList = userTypeService.getAllUserTypes();

        logger.info("fetched total user types: {}", daoList.size());

        List<UserTypeDTO> userTypes = new ArrayList<>();
        for(UserTypeDao dao : daoList) {
            userTypes.add(new UserTypeDTO(dao));
        }

        logger.info("get all user types call completed successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(userTypes));
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addUser(@RequestBody UserTypeDTO userTypeDto) {
        logger.info("add user type called.");
        if(userTypeDto == null || userTypeDto.getUserTypeDesc() == null || userTypeDto.getUserTypeDesc().isEmpty()) {
            logger.error("User Type details are missing. Unable to create new user type");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Type Details missing.");
        }
        userTypeDto.setActive(true);
        userTypeDto.setCreateOn(new Date(System.currentTimeMillis()));
        UserTypeDao userTypeDao = userTypeService.createUserType(new UserTypeDao(userTypeDto));

        if(userTypeDao != null) {
            logger.info("User Type is added successfully.");
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new UserTypeDTO(userTypeDao)));
        }

        logger.error("Unable to create user type.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to create user type.");
    }

    @GetMapping(value = "/get/{userTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserType(@PathVariable String userTypeId) {
        logger.info("get User type called with usertypeId: {}", userTypeId);

        if(userTypeId == null || userTypeId.isEmpty()) {
            logger.error("User Type Id is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Type Id is missing.");
        }
        UserTypeDTO userTypeDTO = null;
        try{
            int id = Integer.parseInt(userTypeId);
            UserTypeDao dao = userTypeService.findById(id);
            if(dao != null) {
                userTypeDTO = new UserTypeDTO(dao);
            }
        } catch(NumberFormatException ex) {
            logger.error("Invalid user id format.", ex);
        }

        if(userTypeDTO == null) {
            logger.info("User Type details not found for userTypeId = {}", userTypeId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to get the user type using id = " + userTypeId);
        }

        logger.info("User Type details fetched. {}", userTypeDTO);
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(userTypeDTO));
    }


    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateUser(@RequestBody UserTypeDTO userTypeDTO) {
        logger.info("update user type called: {}", userTypeDTO.getUserTypeId());

        if(userTypeDTO == null) {
            logger.error("User Type details is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Type details is missing");
        }

        UserTypeDao dao1 = userTypeService.updateUserType(userTypeDTO);
        if(dao1 == null) {
            logger.error("Invalid user type id provided. User type could not be found.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User type details not found.");
        }
        UserTypeDTO d = new UserTypeDTO(dao1);

        logger.info("User Type is updated");
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(d));

    }



}
