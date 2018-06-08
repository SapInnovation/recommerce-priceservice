package com.sapient.retail.price.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sapient.retail.price.common.model.Price;
import com.sapient.retail.price.service.kafka.KafkaPriceService;

@RestController
public class PriceDataController {

	private KafkaPriceService priceService;

	public PriceDataController(KafkaPriceService priceService) {
		this.priceService = priceService;
	}

	@RequestMapping(value = "/pricedata", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void priceData(@RequestBody Price priceData) {
		priceService.sendDataToKafkaTopic(priceData);
	}
	
	@RequestMapping(value = "/createtopic/{topicName}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void createTopic(@PathVariable String topicName) {
		priceService.createTopicInKafka(topicName);
	}

}
