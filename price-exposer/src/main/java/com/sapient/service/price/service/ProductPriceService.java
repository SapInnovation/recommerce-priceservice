package com.sapient.service.price.service;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import com.sapient.retail.price.common.model.ProductPrice;

import reactor.core.publisher.Flux;

/**
 * @author vivsingh1
 *
 */
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
    
	/**
	 * This method will subscribe the FE request to rethink DB change feed
	 * @param productId
	 * @return Flux<ProductPrice>
	 */
	@SuppressWarnings("unchecked")
    public Flux<ProductPrice> registerStream(final String productId) {
        LOGGER.info("Registering RethinkDB Streams for skuId: " + productId);
        return Flux.create(stream ->
                Cursor.class.cast(rethinkDB
                        .table(skuDocument)
                        .filter(doc -> doc.getField("productId").eq(productId))
                        .changes()
                        .run(connection))
                        .forEach(priceUpdate -> stream.next(
                                (new ObjectMapper())
                                        .convertValue(HashMap.class.cast(
                                        		Cursor.class.cast(rethinkDB
                                                        .table(skuDocument)
                                                        .filter(doc -> doc.getField("productId").eq(productId))
                                                        .run(connection)).toList().get(0)),
                                                ProductPrice.class))));
    }
	 
	 
	 /**
	 * @param priceStream
	 */
	public void updatePrice(final ProductPrice priceStream) {
	        HashMap<String, Long> updatedMap = rethinkDB
	                .table(skuDocument)
	                .filter(doc -> doc.getField("skuId")
	                        .eq(priceStream.getProductId()))
	                .update(priceStream)
	                .run(connection);
	        if (0 >= (updatedMap.get("replaced")
	                + updatedMap.get("unchanged"))) {
	        	rethinkDB.table(skuDocument)
	                    .insert(priceStream)
	                    .run(connection);
	            LOGGER.info("Price record added, productId:" + priceStream.getProductId());
	        } else {
	            LOGGER.info("Price record updated, productId:" + priceStream.getProductId());
	        }
	    }
	 
	/**
	 * This method will return the collection(product + SKU) object of passed product Id.
	 * @param productId
	 * @return ProductPrice price
	 */
	public ProductPrice getProductAndSkuData (String productId) {
		
		return (new ObjectMapper())
    	.convertValue(HashMap.class.cast(
    		Cursor.class.cast(rethinkDB
                    .table(skuDocument)
                    .filter(doc -> doc.getField("productId").eq(productId))
                    .run(connection)).toList().get(0)),
            ProductPrice.class);

	}
	 
}
