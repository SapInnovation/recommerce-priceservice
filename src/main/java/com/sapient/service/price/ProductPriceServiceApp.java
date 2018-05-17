package com.sapient.service.price;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProductPriceServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(ProductPriceServiceApp.class, args);
	}
}
