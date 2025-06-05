package com.syu.cara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class CaraApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaraApplication.class, args);
	}

}
