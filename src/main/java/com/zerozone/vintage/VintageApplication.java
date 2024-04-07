package com.zerozone.vintage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class VintageApplication {

	public static void main(String[] args) {
		SpringApplication.run(VintageApplication.class, args);
	}

}
