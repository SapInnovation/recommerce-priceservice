package com.sapient.service.price.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.SubscribableChannel;

@Configuration
public class PriceConfiguration {
	
	@Bean
    SubscribableChannel priceChannel() {
        return MessageChannels.publishSubscribe().get();
    }

}
