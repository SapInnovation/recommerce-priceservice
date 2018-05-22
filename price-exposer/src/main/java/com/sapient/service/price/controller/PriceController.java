package com.sapient.service.price.controller;

import com.sapient.retail.price.common.model.Price;
import com.sapient.service.price.exception.ProductNotFoundException;
import com.sapient.service.price.payload.ErrorResponse;
import com.sapient.service.price.service.ProductPriceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

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
    @GetMapping(value = "/stream/price/{skuId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Price> streamAllPrices(@PathVariable(value = "skuId") String skuId) {
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
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> productNotFoundException(ProductNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
