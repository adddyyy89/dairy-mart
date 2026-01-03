package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.LedgerTransactionsDao;
import com.dairymart.dairyappserver.dao.RetailOrderDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.LedgerTransactionsDTO;
import com.dairymart.dairyappserver.dto.RetailOrderDTO;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.service.LedgerService;
import com.dairymart.dairyappserver.service.RetailOrderService;
import com.dairymart.dairyappserver.service.UserService;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUsersController {

    Logger logger = LoggerFactory.getLogger(AdminUsersController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private UserService userService;


    @CrossOrigin(origins = "*")
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAdminDashboardData() {
        logger.info("Get Admin Users data");

        List<UserDao> userDaos = userService.getAllUsers();
        List<UserDTO> userDTOS = new ArrayList<>(userDaos.size());
        for(UserDao userDao : userDaos) {
            userDTOS.add(new UserDTO(userDao));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("users", userDTOS);

        logger.info("Get Admin Users data completed.");

        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(jsonObject));
    }



}
