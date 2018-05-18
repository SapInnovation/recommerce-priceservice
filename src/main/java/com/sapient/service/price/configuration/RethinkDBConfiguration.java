package com.sapient.service.price.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.sapient.service.price.connection.DbInitializer;
import com.sapient.service.price.connection.RethinkDBConnectionFactory;

@Configuration
public class RethinkDBConfiguration {
	
	@Autowired
    private Environment env;

    public static String DBHOST = "127.0.0.1";
    public static int port = 28015;
    
    private static final RethinkDB r = RethinkDB.r;
    
    @Bean
    public RethinkDB rethinkDB() {
        return r;
    }

    @Bean
    public Connection rethinkDBConnection() {
        return r.connection()
                .hostname(this.env.getProperty("rethinkdb.dbhost"))
                .port(Integer.valueOf(this.env.getProperty("rethinkdb.port")))
                .connect();
        }

}
