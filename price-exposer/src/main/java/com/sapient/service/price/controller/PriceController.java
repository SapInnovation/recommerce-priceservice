package com.sapient.service.price.controller;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sapient.retail.price.common.model.ProductPrice;
import com.sapient.service.price.exception.ProductNotFoundException;
import com.sapient.service.price.payload.ErrorResponse;
import com.sapient.service.price.service.ProductPriceService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by hgupta.
 */
@RestController
public class PriceController {

    private ProductPriceService priceService;

    public PriceController(final ProductPriceService priceService) {
        this.priceService = priceService;
    }

    // price updates are Sent to the client as Server Sent Events
    @GetMapping(value = "/stream/price/{productId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductPrice> streamAllPrices(@PathVariable(value = "productId") String productId) {
        return priceService.registerStream(productId);
    }

    @PostMapping(value = "/stream/createPrice")
    public Mono<ProductPrice> createStock(@Valid @RequestBody ProductPrice priceStream) {
        priceService.updatePrice(priceStream);
        return Mono.just(priceStream);
    }

    /*
        Exception Handling Examples (These can be put into a @ControllerAdvice to handle exceptions globally)
    */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> productNotFoundException(ProductNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
