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

    // price updates are Sent to the client as Server Sent Events
    @GetMapping(value = "/stream/price/{skuId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Price> streamAllPrices(@PathVariable(value = "skuId") String skuId) {
        return Flux.create(stream -> {
            MessageHandler handler = msg -> {
                final Price price = Price.class.cast(msg.getPayload());
                LOGGER.info("Request skuId:" + skuId + ", Current Event productId:" + price.getSkuId());
                if (skuId.equalsIgnoreCase(price.getSkuId()))
                    stream.next(price);
            };
            stream.onCancel(() -> priceChannel.unsubscribe(handler));
            priceChannel.subscribe(handler);
        });
    	
    	//return registerStream(skuId);
    }
    
    
    
    private static final RethinkDB r = RethinkDB.r;
    @Autowired
	private RethinkDBConnectionFactory connection;
    
    @SuppressWarnings("unchecked")
    public Flux<Price> registerStream(final long skuId) {
        LOGGER.info("Registering RethinkDB Streams for skuId: " + skuId);
        return Flux.create(stream ->
                Cursor.class.cast(r
                        .db("pricedata")
                        .table("price")
                        .filter(doc -> doc.getField("skuId").eq(skuId))
                        .changes()
                        .run(connection.createConnection()))
                        .forEach(priceUpdate -> stream.next(
                                (new ObjectMapper())
                                        .convertValue(HashMap.class.cast(
                                                Cursor.class.cast(r
                                                        .db("pricedata")
                                                        .table("price")
                                                        .filter(doc -> doc.getField("skuId").eq(skuId))
                                                        .run(connection.createConnection())).toList().get(0)),
                                                Price.class))));
    }
    
    
    @PostMapping(value = "/stream/createPrice")
    public Mono<Price> createStock(@Valid @RequestBody Price priceStream) {
        updatePrice(priceStream);
        return Mono.just(priceStream);
    }
    
    public void updatePrice(final Price priceStream) {
        HashMap<String, Long> updatedMap = r.db("pricedata")
                .table("price")
                .filter(doc -> doc.getField("skuId")
                        .eq(priceStream.getSkuId()))
                .update(priceStream)
                .run(connection.createConnection());
        if (0 >= (updatedMap.get("replaced")
                + updatedMap.get("unchanged"))) {
            r.table("stream")
                    .insert(priceStream)
                    .run(connection.createConnection());
            LOGGER.info("Price record added, skuId:" + priceStream.getSkuId());
        } else {
            LOGGER.info("Price record updated, skuId:" + priceStream.getSkuId());
        }
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
