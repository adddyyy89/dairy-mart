package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.CrateDao;
import com.dairymart.dairyappserver.dao.CratePoolDao;
import com.dairymart.dairyappserver.dto.CrateDTO;
import com.dairymart.dairyappserver.dto.CrateMovementDTO;
import com.dairymart.dairyappserver.service.CrateService;
import com.dairymart.dairyappserver.util.ApiMessages;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/crate")
@Tag(name = "Crates", description = "Crate pool, assignment, store send/return, and branch return")
public class CrateController {

    Logger logger = LoggerFactory.getLogger(CrateController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private CrateService crateService;

    @Operation(summary = "All crate holder rows")
    @GetMapping(value = "/get/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllCrates() {
        List<CrateDTO> crateDTOS = new ArrayList<>();
        for (CrateDao crate : crateService.getTotalCrates()) {
            crateDTOS.add(new CrateDTO(crate));
        }
        logger.info("Get all crates completed, total={}", crateDTOS.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(crateDTOS));
    }

    @Operation(summary = "Crate history for one user")
    @GetMapping(value = "/get/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCrateDetailsByUserId(@PathVariable String userId) {
        int user;
        try {
            user = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiMessages.of("Invalid user id provided."));
        }
        List<CrateDTO> crateDTOS = new ArrayList<>();
        for (CrateDao crate : crateService.getTotalCratesForUser(user)) {
            crateDTOS.add(new CrateDTO(crate));
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(crateDTOS));
    }

    @Operation(summary = "Update crate quantities for a user")
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCrate(@RequestBody CrateDTO crateDTO) {
        if (crateDTO == null || crateDTO.getUserId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiMessages.of("User details are missing."));
        }
        try {
            CrateDao crateDao = crateService.updateCrate(new CrateDao(crateDTO));
            if (crateDao == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to update crate details.");
            }
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(new CrateDTO(crateDao)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiMessages.of(ex.getMessage()));
        }
    }

    @Operation(summary = "Current assigned crate count for a salesman")
    @GetMapping(value = "/assigned/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAssignedCrates(@PathVariable String userId) {
        int id;
        try {
            id = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiMessages.of("Invalid user id provided."));
        }
        int cratesAssigned = crateService.getCurrentAssignedCrateForUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(cratesAssigned + "");
    }

    @Operation(summary = "Pool stock plus crates held by salesmen and stores")
    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> summary() {
        try {
            return ResponseEntity.ok(gson.toJson(crateService.getSummary()));
        } catch (Exception ex) {
            logger.error("Crate summary failed", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not load crate summary. Ensure crate_pool exists (docs/sql/create_crate_pool.sql).");
        }
    }

    @Operation(summary = "Add crates to the branch pool")
    @PostMapping(value = "/pool/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addToPool(@RequestBody CrateMovementDTO dto) {
        try {
            CratePoolDao pool = crateService.addToPool(dto.getQuantity());
            return ResponseEntity.ok(gson.toJson(pool));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiMessages.of(ex.getMessage()));
        }
    }

    @Operation(summary = "Assign pool crates to a salesman")
    @PostMapping(value = "/assign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> assignToSalesman(@RequestBody CrateMovementDTO dto) {
        try {
            CrateDao dao = crateService.assignToSalesman(dto.getSalesmanId(), dto.getQuantity());
            return ResponseEntity.ok(gson.toJson(new CrateDTO(dao)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiMessages.of(ex.getMessage()));
        }
    }

    @Operation(summary = "Salesman sends crates to a store")
    @PostMapping(value = "/store/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendToStore(@RequestBody CrateMovementDTO dto) {
        try {
            CrateDao dao = crateService.sendToStore(dto);
            return ResponseEntity.ok(gson.toJson(new CrateDTO(dao)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiMessages.of(ex.getMessage()));
        }
    }

    @Operation(summary = "Store returns crates to a salesman")
    @PostMapping(value = "/store/return", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> returnFromStore(@RequestBody CrateMovementDTO dto) {
        try {
            CrateDao dao = crateService.returnFromStore(dto);
            return ResponseEntity.ok(gson.toJson(new CrateDTO(dao)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiMessages.of(ex.getMessage()));
        }
    }

    @Operation(summary = "Salesman returns crates to the branch pool")
    @PostMapping(value = "/branch/return", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> returnToBranch(@RequestBody CrateMovementDTO dto) {
        try {
            CrateDao dao = crateService.returnToBranch(dto.getSalesmanId(), dto.getQuantity());
            return ResponseEntity.ok(gson.toJson(new CrateDTO(dao)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiMessages.of(ex.getMessage()));
        }
    }
}
