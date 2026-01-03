package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.dao.TrackingDao;
import com.dairymart.dairyappserver.dto.TrackingDTO;
import com.dairymart.dairyappserver.service.TrackingService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tracking")
public class TrackingController {

    Logger logger = LoggerFactory.getLogger(TrackingController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private TrackingService trackingService;

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCrateDetailsByUserId(@RequestParam int userId, @RequestParam String date) {
        logger.info("Get crate details for user id: {}", userId);
        List<TrackingDTO> trackingDTOS = new ArrayList<>();
        try {
            Date trackingDate = new Date(new SimpleDateFormat("dd-MM-yyyy").parse(date).getTime());
            List<TrackingDao> trackingDaos = trackingService.getTrackingLocationsByDate(trackingDate, userId);

            for(TrackingDao trackingDao : trackingDaos) {
                trackingDTOS.add(new TrackingDTO(trackingDao));
            }

        } catch(ParseException e) {
            logger.error("Unable to parse the input date. Provided date is not in ddMMyyyy format!!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson("Unable to parse the input date. Provided date is not in ddMMyyyy format!!"));
        }
        logger.info("get location details call successful. Location data fetched size : {}", trackingDTOS.size());
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(trackingDTOS));
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCrate(@RequestBody TrackingDTO trackingDTO) {
        logger.info("update tracking location called.");
        TrackingDao trackingDao = new TrackingDao();
        try {
            trackingDao = trackingService.updateLocation(new TrackingDao(trackingDTO));
        } catch(ParseException e) {
            logger.error("unable to parse input date.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson("Unable to parse date"));
        }
        logger.info("update tracking location completed successfully.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(new TrackingDTO(trackingDao)));

    }

}
