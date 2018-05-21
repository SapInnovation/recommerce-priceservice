package com.sapient.service.price.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import com.sapient.retail.price.common.model.Price;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;

@Service
public class ProductPriceService {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private final RethinkDB rethinkDB;
	private final Connection connection;

	@Value(value = "${rethinkdb.sku}")
    private String skuDocument;
	
	
    public ProductPriceService(RethinkDB rethinkDB, Connection connection) {
		this.rethinkDB = rethinkDB;
		this.connection = connection;
	}
    
	@SuppressWarnings("unchecked")
    public Flux<Price> registerStream(final String skuId) {
        LOGGER.info("Registering RethinkDB Streams for skuId: " + skuId);
        return Flux.create(stream ->
                Cursor.class.cast(rethinkDB
                        .table(skuDocument)
                        .filter(doc -> doc.getField("skuId").eq(skuId))
                        .changes()
                        .run(connection))
                        .forEach(priceUpdate -> stream.next(
                                (new ObjectMapper())
                                        .convertValue(HashMap.class.cast(
                                        		Cursor.class.cast(rethinkDB
                                                        .table(skuDocument)
                                                        .filter(doc -> doc.getField("skuId").eq(skuId))
                                                        .run(connection)).toList().get(0)),
                                                Price.class))));
    }
	 
	 
	 public void updatePrice(final Price priceStream) {
	        HashMap<String, Long> updatedMap = rethinkDB
	                .table(skuDocument)
	                .filter(doc -> doc.getField("skuId")
	                        .eq(priceStream.getSkuId()))
	                .update(priceStream)
	                .run(connection);
	        if (0 >= (updatedMap.get("replaced")
	                + updatedMap.get("unchanged"))) {
	        	rethinkDB.table(skuDocument)
	                    .insert(priceStream)
	                    .run(connection);
	            LOGGER.info("Price record added, skuId:" + priceStream.getSkuId());
	        } else {
	            LOGGER.info("Price record updated, skuId:" + priceStream.getSkuId());
	        }
	    }
	 
}
