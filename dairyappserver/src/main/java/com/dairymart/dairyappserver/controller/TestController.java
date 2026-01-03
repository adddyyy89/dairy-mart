package com.dairymart.dairyappserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping(path = "/test")
    public String test() {
        logger.info("Test api called.");
        return "This is a test";
    }



}
