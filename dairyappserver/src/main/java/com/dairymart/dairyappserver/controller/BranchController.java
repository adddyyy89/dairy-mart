package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.BranchDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.BranchDTO;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.service.BranchService;
import com.dairymart.dairyappserver.service.UserService;
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
@RequestMapping("/branch")
public class BranchController {

    Logger logger = LoggerFactory.getLogger(BranchController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private BranchService branchService;

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addUser(@RequestBody BranchDTO branchDTO) {
        logger.info("add branch called.");
        if(branchDTO == null || branchDTO.getBranchName().isEmpty()) {
            logger.error("Branch details are missing. Unable to create new branch");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Branch Details missing.");
        }
        branchDTO.setCreatedOn(new Date(System.currentTimeMillis()));
        branchDTO.setLastUpdated(new Date(System.currentTimeMillis()));
        BranchDao branchDao = branchService.createBranch(new BranchDao(branchDTO));
        if(branchDao != null) {
            logger.info("Branch is added successfully.");
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new BranchDTO(branchDao)));
        }

        logger.error("Unable to create branch.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to create branch.");

    }

    @GetMapping(value = "/get/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getBranch(@PathVariable String branchId) {
        logger.info("getBranch called with branchId: {}", branchId);

        if(branchId != null && branchId.equalsIgnoreCase("all")) {
            List<BranchDao> daoList = branchService.getAllBranches();
            List<BranchDTO> branchDTOS = new ArrayList<>();
            for(BranchDao dao : daoList) {
                branchDTOS.add(new BranchDTO(dao));
            }

            logger.info("All branches fetched, total: {}", branchDTOS.size());
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(branchDTOS));
        }
        BranchDTO branchDTO = null;
        try{
            int id = Integer.parseInt(branchId);
            BranchDao dao = branchService.findById(id);
            if(dao != null) {
                branchDTO = new BranchDTO(dao);
            }
        } catch(NumberFormatException ex) {
            logger.error("Invalid user id format.", ex);
        }

        if(branchDTO == null) {
            logger.info("Branch details not found for branchId = {}", branchId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to get the branch using id = " + branchId);
        }

        logger.info("Branch details fetched. {}", branchDTO);
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(branchDTO));
    }


    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateBranch(@RequestBody BranchDTO branchDTO) {
        logger.info("update branch called: {}", branchDTO.getBranchId());

        if(branchDTO == null) {
            logger.error("Branch details is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Branch details is missing");
        }

        BranchDao dao1 = branchService.updateById(branchDTO);
        if(dao1 == null) {
            logger.info("Invalid branch id provided. Branch could not be found.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Branch details not found.");
        }
        BranchDTO d = new BranchDTO(dao1);

        logger.info("Branch is updated");
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(d));

    }





}
