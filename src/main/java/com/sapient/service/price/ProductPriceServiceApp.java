package com.sapient.service.price;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class ProductPriceServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(ProductPriceServiceApp.class, args);
	}
}
