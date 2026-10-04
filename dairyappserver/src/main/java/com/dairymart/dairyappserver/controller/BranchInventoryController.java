package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dto.InventoryItemDTO;
import com.dairymart.dairyappserver.service.BranchInventoryService;
import com.dairymart.dairyappserver.util.ApiMessages;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory", description = "Branch on-hand stock for Dairy Mart products")
public class BranchInventoryController {

    private static final Gson gson = new Gson();

    @Autowired
    private BranchInventoryService inventoryService;

    @Operation(summary = "List on-hand quantity for every active product at a branch")
    @GetMapping(value = "/branch/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> byBranch(@PathVariable int branchId) {
        return ResponseEntity.ok(gson.toJson(inventoryService.listForBranch(branchId)));
    }

    @Operation(summary = "Set on-hand quantity for one product at a branch")
    @PostMapping(value = "/set", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setQuantity(@RequestBody InventoryItemDTO dto) {
        try {
            return ResponseEntity.ok(gson.toJson(
                    inventoryService.setQuantity(dto.getBranchId(), dto.getProductId(), dto.getQuantity())));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiMessages.of(ex.getMessage()));
        }
    }
}
