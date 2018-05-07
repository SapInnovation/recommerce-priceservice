package com.sapient.service.price.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.sapient.service.price.exception.ProductNotFoundException;
import com.sapient.service.price.model.Price;
import com.sapient.service.price.payload.ErrorResponse;
import com.sapient.service.price.repository.PriceRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by hgupta.
 */
@RestController
public class PriceController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SubscribableChannel priceChannel;

    @Autowired
    private PriceRepository priceRepository;

    @GetMapping("/price")
    public Flux<Price> getAllPrices() {
        return priceRepository.findAll();
    }

    @PostMapping("/price")
    public Mono<Price> createPrices(@Valid @RequestBody Price price) {
        return priceRepository.save(price);
    }

    @GetMapping("/price/{productId}")
    public Mono<ResponseEntity<Price>> getPriceById(@PathVariable(value = "productId") String productId) {
        return priceRepository.findById(productId)
                .map(savedPrice -> ResponseEntity.ok(savedPrice))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/price/{productId}")
    public Mono<ResponseEntity<Price>> updatePrice(@PathVariable(value = "productId") String productId,
                                                   @Valid @RequestBody Price price) {
        return priceRepository.findById(productId)
                .flatMap(existingPrice -> {
                    existingPrice.setPrice(price.getPrice());
                    return priceRepository.save(existingPrice);
                })
                .map(updatePrice -> new ResponseEntity<>(updatePrice, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/price/{productId}")
    public Mono<ResponseEntity<Void>> deletePrice(@PathVariable(value = "productId") String productId) {

        return priceRepository.findById(productId)
                .flatMap(existingPrice ->
                        priceRepository.delete(existingPrice)
                            .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // price updates are Sent to the client as Server Sent Events
    @GetMapping(value = "/stream/price/{productId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Price> streamAllPrices(@PathVariable(value = "productId") String productId) {
        return Flux.create(stream -> {
            MessageHandler handler = msg -> {
                final Price price = Price.class.cast(msg.getPayload());
                LOGGER.info("Request skuId:" + productId + ", Current Event productId:" + price.getProductId());
                if (productId == price.getProductId())
                    stream.next(price);
            };
            stream.onCancel(() -> priceChannel.unsubscribe(handler));
            priceChannel.subscribe(handler);
        });
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
