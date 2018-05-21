package com.sapient.service.price.listener;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Cursor;
import com.sapient.service.price.connection.RethinkDBConnectionFactory;
import com.sapient.service.price.model.Price;
import com.sapient.service.price.service.ProductPriceService;

@Service
public class DbChangeListener {
	
	protected final Logger log = LoggerFactory.getLogger(DbChangeListener.class);
    /*private static final RethinkDB r = RethinkDB.r;

    @Autowired
    private RethinkDBConnectionFactory connectionFactory;

    @Autowired
    private ProductPriceService priceService;*/
    
    /*@Async
    public void pushChangesToPriceChannel () throws TimeoutException {
    	
    	Cursor<Price> cursor = r.db("pricedata").table("price").changes()
                .run(connectionFactory.createConnection(),Price.class);
    	List users = cursor.toList();
    	System.out.println("list::" + users);
    	Price price = new ObjectMapper().convertValue(users, Price.class);
    	priceService.publishPrice(price);
    	for (Object doc : cursor) {
    	    //System.out.println("doc:: " + doc + "list::" + users);
    	}
    	while(cursor.hasNext()) {
    		Price price = cursor.next(30);
    		System.out.println("data:::" + price.getSkuId() + ":" + price.getPrice());
    		priceService.publishPrice(price);
    	}
    	
    	
    	
    	cursor.forEach(priceupdate -> priceService.publishPrice(
    			(new ObjectMapper())
    			.convertValue(HashMap.class.cast(
    					Cursor.getClass().cast(
    							r.db("pricedata")
    							.table("price")
    							.run(connectionFactory.createConnection())
    							).toList()),
    							Price.class))
    	);

}*/
    
}
