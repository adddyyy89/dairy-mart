package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dao.UserLoginDao;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.dto.UserLoginDTO;
import com.dairymart.dairyappserver.dto.UserLoginResetDTO;
import com.dairymart.dairyappserver.service.LoginService;
import com.dairymart.dairyappserver.service.TwilioService;
import com.dairymart.dairyappserver.service.UserService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginSvc;

    @Autowired
    private UserService userSvc;

    Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static final Gson gson = new Gson();

    @CrossOrigin("*")
    @PostMapping(value="/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestBody UserLoginDTO dto) {
        logger.info("login called using phone: " + dto.getPhoneNumber());
        if (dto == null || dto.getPassword() == null && dto.getPassword().isEmpty()) {
            logger.warn("Login details not provided correctly.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }

        logger.info("Checking role of phoneNumber: " + dto.getPhoneNumber());

        // Set other details
        dto.setRole(userSvc.findRoleByPhone(dto.getPhoneNumber()));
        logger.info("Role found : " + dto.getRole());

        dto.setIsActive(true);
        dto.setLoggedIn(new Timestamp(System.currentTimeMillis()));

        // get userid of the phone number
        UserDao dao = userSvc.findByPhone(dto.getPhoneNumber());
        dto.setUserId(dao.getUserId());

        // check if already logged in
        UserLoginDao loggedInDao = loginSvc.isLoggedIn(new UserLoginDao(dto));
        if(loggedInDao != null) {
            logger.error("User tried to login using phone: {} but is already logged in!!", dto.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new UserLoginDTO(loggedInDao)));

        }

        // Create entry of login
        UserLoginDao loginDao = loginSvc.login(new UserLoginDao(dto));
        logger.info("Login successful for phoneNumber: {}", dto.getPhoneNumber());

        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new UserLoginDTO(loginDao)));

    }

    @CrossOrigin("*")
    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> logout(@RequestBody UserLoginDTO dto) {
        logger.info("logout called using phone: {}", dto.getPhoneNumber());
        // logout using phone number
        UserLoginDao savedLogin = loginSvc.isLoggedIn(new UserLoginDao(dto));

        if(savedLogin != null) {
            UserLoginDao savedDao = null;
            if((dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty()) && dto.getUserId() > 0) {

                // logout using userid
                savedLogin.setActive(false);
                savedLogin.setLoggedOut(new Timestamp(System.currentTimeMillis()));
                savedDao = loginSvc.logout(savedLogin);
            }
            else if(dto.getUserId() <= 0 && !dto.getPhoneNumber().isEmpty()) {
                // logout using password
                savedLogin.setUserId(savedLogin.getUserId());
                savedLogin.setLoggedOut(new Timestamp(System.currentTimeMillis()));
                savedLogin.setActive(false);
                savedDao = loginSvc.logout(savedLogin);
            }
            else {
                logger.error("No user id or phone number provided for logout!!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson("No user id or phone number provided for logout!!"));
            }

            logger.info("User id {} is logged out successfully!", savedDao.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new UserLoginDTO(savedDao)));
        }

        logger.error("There is no entry of login for requested details!");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson("There is no entry of login for requested details!"));
    }

    @PostMapping(value = "/reset", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> resetPassword(@RequestBody UserLoginResetDTO dto) {
        logger.info("Password reset api called.");

        UserDao dao = userSvc.findByPhone(dto.getPhoneNumber());
        dao.setPassword(dto.getNewPassword());
        UserDao userDao = userSvc.saveUser(dao);

        if(userDao.getPassword().equals(dto.getNewPassword())) {
            logger.info("Password update successful for phone : {} ", dto.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(dao));
        }
        else {
            logger.error("Password update failed for phone : {}", dto.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson("Failed to update password."));
        }
    }
}
