package com.sapient.service.price.connection;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.sapient.service.price.listener.DbChangeListener;

public class DbInitializer implements InitializingBean{
	
	/*//@Autowired
	//private RethinkDBConnectionFactory connectionFactory;
	
	private final Connection connection;
	
	
	
	public DbInitializer(Connection connection) {
		this.connection = connection;
	}


	private static final RethinkDB r = RethinkDB.r;*/
	
	@Override
    public void afterPropertiesSet() throws Exception {
        //createDb();
        //dbChangesListener.pushChangesToPriceChannel();
    }
	
	
	/*private void createDb() {
        //Connection connection = connectionFactory.createConnection();
        List<String> dbList = r.dbList().run(connection);
        if (!dbList.contains("pricedata")) {
            r.dbCreate("pricedata").run(connection);
        }
        List<String> tables = r.db("pricedata").tableList().run(connection);
        if (!tables.contains("price")) {
            r.db("pricedata").tableCreate("price").run(connection);
            r.db("pricedata").table("price").indexCreate("skuId").run(connection);
        }
    }*/
}
