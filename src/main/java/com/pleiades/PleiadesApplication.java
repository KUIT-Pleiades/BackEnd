package com.pleiades;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
public class PleiadesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PleiadesApplication.class, args);
	}

}
