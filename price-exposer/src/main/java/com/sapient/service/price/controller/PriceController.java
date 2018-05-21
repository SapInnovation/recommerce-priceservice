package com.sapient.service.price.controller;

import java.time.Duration;
import java.util.HashMap;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import com.sapient.service.price.connection.RethinkDBConnectionFactory;
import com.sapient.service.price.exception.ProductNotFoundException;
import com.sapient.service.price.model.Price;
import com.sapient.service.price.payload.ErrorResponse;
import com.sapient.service.price.service.ProductPriceService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by hgupta.
 */
@RestController
public class PriceController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	
	private SubscribableChannel priceChannel;
	private ProductPriceService priceService;
	
	

    public PriceController(SubscribableChannel priceChannel, final ProductPriceService priceService) {
		this.priceChannel = priceChannel;
		this.priceService = priceService;
	}

	// price updates are Sent to the client as Server Sent Events
    @GetMapping(value = "/stream/price/{skuId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Price> streamAllPrices(@PathVariable(value = "skuId") String skuId) {
        /*return Flux.create(stream -> {
            MessageHandler handler = msg -> {
                final Price price = Price.class.cast(msg.getPayload());
                LOGGER.info("Request skuId:" + skuId + ", Current Event productId:" + price.getSkuId());
                if (skuId.equalsIgnoreCase(price.getSkuId()))
                    stream.next(price);
            };
            stream.onCancel(() -> priceChannel.unsubscribe(handler));
            priceChannel.subscribe(handler);
        });*/
    	
    	return priceService.registerStream(skuId);
    }
    
    @PostMapping(value = "/stream/createPrice")
    public Mono<Price> createStock(@Valid @RequestBody Price priceStream) {
        priceService.updatePrice(priceStream);
        return Mono.just(priceStream);
    }
    
    

    /*
        Exception Handling Examples (These can be put into a @ControllerAdvice to handle exceptions globally)
    */

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKeyException(DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("A ProductPrice with the same Price already exists"));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> productNotFoundException(ProductNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

}
