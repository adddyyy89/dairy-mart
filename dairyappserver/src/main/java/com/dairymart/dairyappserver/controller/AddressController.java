package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.BranchDao;
import com.dairymart.dairyappserver.dao.CityDao;
import com.dairymart.dairyappserver.dao.StateDao;
import com.dairymart.dairyappserver.dto.BranchDTO;
import com.dairymart.dairyappserver.dto.CityDTO;
import com.dairymart.dairyappserver.dto.StateDTO;
import com.dairymart.dairyappserver.service.BranchService;
import com.dairymart.dairyappserver.service.CityService;
import com.dairymart.dairyappserver.service.StateService;
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
@RequestMapping("/address")
public class AddressController {

    Logger logger = LoggerFactory.getLogger(AddressController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private StateService stateService;

    @Autowired
    private CityService cityService;

    @GetMapping(value = "/state/get/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllStates() {
        logger.info("Get all states service called.");

        List<StateDTO> stateDTOS = new ArrayList<>();
        List<StateDao> states = stateService.getAllStates();
        for(StateDao state : states) {
            stateDTOS.add(new StateDTO(state));
        }

        logger.info("Get all states service completed, Total states fetched: {}", stateDTOS.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(stateDTOS));
    }

    @GetMapping(value = "/city/getbystate")
    public ResponseEntity<String> getCitiesByState(@RequestBody StateDTO stateDTO) {
        logger.info("Get cities by state service called.");

        if(stateDTO.getStateId() < 0) {
            logger.error("Invalid state id provided!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson("Invalid state id provided."));
        }

        List<CityDao> cities = cityService.getCitiesForState(stateDTO.getStateId());
        List<CityDTO> cityDTOS = new ArrayList<>();
        for(CityDao city : cities) {
            cityDTOS.add(new CityDTO(city));
        }

        logger.info("Get cities by state service completed. Total cities fetched for state id: {} are {}", stateDTO.getStateId(), cityDTOS.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(cityDTOS));
    }



}
