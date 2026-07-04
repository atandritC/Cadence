package com.cadence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CadenceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CadenceApplication.class, args);
	}

}
