package com.sapient.service.price.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.sapient.service.price.connection.DbInitializer;
import com.sapient.service.price.connection.RethinkDBConnectionFactory;

@Configuration
public class RethinkDBConfiguration {
	
	@Autowired
    private Environment env;

    public static String DBHOST = "127.0.0.1";

    @PostConstruct
    public void init() {
        this.DBHOST = this.env.getProperty("rethinkdb.dbhost");
    }

    @Bean
    public RethinkDBConnectionFactory connectionFactory() {
        return new RethinkDBConnectionFactory(DBHOST);
    }

    @Bean
    DbInitializer dbInitializer() {
        return new DbInitializer();
    }

}
