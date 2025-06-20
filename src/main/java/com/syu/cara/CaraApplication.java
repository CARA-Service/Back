package com.syu.cara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class) // 베포시 미적용
//@SpringBootApplication()
public class CaraApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaraApplication.class, args);
	}

}
