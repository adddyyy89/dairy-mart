package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.CrateDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dao.UserWalletDao;
import com.dairymart.dairyappserver.dto.CrateDTO;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.dto.UserWalletDTO;
import com.dairymart.dairyappserver.service.CrateService;
import com.dairymart.dairyappserver.service.UserService;
import com.dairymart.dairyappserver.service.UserWalletService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpsServer;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private UserService userService;

    @Autowired
    private UserWalletService walletService;

    @Autowired
    private CrateService crateService;

    @CrossOrigin("*")
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addUser(@RequestBody UserDTO userDto) {
        logger.info("add user called.");
        if(userDto == null || userDto.getFirstName().isEmpty() || userDto.getPhoneNumber().isEmpty() || userDto.getPassword().isEmpty()) {
            logger.error("User details are missing. Unable to create new user");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Details missing.");
        }

        UserDao userDao = userService.createUser(new UserDao(userDto));
        if(userDao != null) {
            logger.info("User is added successfully.");

            UserWalletDTO walletDTO = new UserWalletDTO();
            walletDTO.setBalance(0);
            walletDTO.setUserId(userDao.getUserId());
            walletDTO.setOutstanding(0);
            walletDTO.setCreatedOn(userDao.getCreatedOn());
            walletDTO.setLastUpdated(userDao.getCreatedOn());
            walletDTO.setCreatedBy(userDao.getCreatedBy());

            UserWalletDao walletDao = walletService.addWalletDetails(new UserWalletDao(walletDTO));
            if(walletDao != null) {
                logger.info("User Wallet is created.");
            }
            else {
                logger.error("ERROR: User wallet not created for user: " + userDao.getUserId());
            }

            // Add crate
            CrateDTO crateDTO = new CrateDTO();
            crateDTO.setCrateCount(0);
            crateDTO.setCrateReceived(0);
            crateDTO.setUserId(userDao.getUserId());
            crateDTO.setUserTypeId(userDao.getTypeId());
            crateDTO.setRecordTimestamp(new Timestamp(System.currentTimeMillis()));

            CrateDao crateDao = crateService.updateCrate(new CrateDao(crateDTO));
            if(crateDao != null) {
                logger.info("Crate details is created.");
            }
            else {
                logger.error("ERROR: Crate details not added for this user: " + userDao.getUserId());
            }


            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new UserDTO(userDao)));
        }

        logger.error("Unable to create user.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to create user.");

    }

    @CrossOrigin("*")
    @PostMapping(value = "/add/admin", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addAdminUser(@RequestBody UserDTO userDTO) {
        if(userDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Details missing.");
        }

        if(userService.validateAdminUser(userDTO)) {
            UserDao userDao = userService.createUser(new UserDao(userDTO));
            if(userDao != null) {
                return ResponseEntity.status(HttpStatus.OK).body("Admin user is added.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to add admin user.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Details are not correct");
    }

    @CrossOrigin("*")
    @GetMapping(value = "/get/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUser(@PathVariable String userId) {
        logger.info("getUser called with userId: {}", userId);

        if(userId == null || userId.isEmpty()) {
            logger.error("User Id is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Id is missing.");
        }
        UserDTO userDTO = null;
        try{
            int id = Integer.parseInt(userId);
            UserDao dao = userService.findById(id);
            if(dao != null) {
                userDTO = new UserDTO(dao);
            }
        } catch(NumberFormatException ex) {
            logger.error("Invalid user id format.", ex);
        }

        if(userDTO == null) {
            logger.info("User details not found for userId = {}", userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to get the user using id = " + userId);
        }

        logger.info("User details fetched. {}", userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(userDTO));
    }

    @CrossOrigin("*")
    @GetMapping(value = "/get/usertype/{userTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUsersByType(@PathVariable String userTypeId) {
        logger.info("getUsersByType called with userTypeId: {}", userTypeId);

        if(userTypeId == null || userTypeId.isEmpty()) {
            logger.error("User Type Id is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user Type is missing");
        }
        List<UserDTO> dtoList = new ArrayList<>();
        try{
            int id = Integer.parseInt(userTypeId);
            List<UserDao> daoList = userService.findByTypeId(id);
            if(daoList != null && !daoList.isEmpty()) {
                for(UserDao dao : daoList) {
                    dtoList.add(new UserDTO(dao));
                }
            }
        } catch(NumberFormatException ex) {
            logger.error("Invalid user id format.", ex);
        }

        if(dtoList.isEmpty()) {
            logger.info("User details not found for userTypeId = {}", userTypeId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User details not found for userTypeId =" + userTypeId);
        }

        logger.info("User details fetched of size = {}", dtoList.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(dtoList));
    }

    @CrossOrigin("*")
    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateUser(@RequestBody UserDTO userDTO) {
        logger.info("update user called: {}", userDTO.getUserId());

        if(userDTO == null) {
            logger.error("User details is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User details is missing");
        }

        UserDao dao1 = userService.updateById(userDTO);
        if(dao1 == null) {
            logger.info("Invalid user id provided. User could not be found/");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User details not found.");
        }
        UserDTO d = new UserDTO(dao1);

        logger.info("User is updated");
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(d));

    }

    @CrossOrigin("*")
    @GetMapping(value = "/get/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUser() {
        logger.info("get All User called");

        List<UserDao> users = userService.getAllUsers();
        List<UserDTO> userDTOS = new ArrayList<>();
        for(UserDao dao : users) {
            userDTOS.add(new UserDTO(dao));
        }

        logger.info("Users fetched. Total Users = {}", userDTOS.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(userDTOS));
    }

}
