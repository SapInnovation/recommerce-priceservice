package com.sapient.service.price.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Publisher;
import org.springframework.stereotype.Service;

import com.sapient.service.price.model.Price;

@Service
public class ProductPriceService {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Publisher(channel = "priceChannel")
    public Price publishPrice(final Price price) {
        LOGGER.info("Publishing price Update Event");
        return price;
    }
}
