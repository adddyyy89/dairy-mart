package com.dairymart.dairyappserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DairyMartAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(DairyMartAppApplication.class, args);
	}

}
