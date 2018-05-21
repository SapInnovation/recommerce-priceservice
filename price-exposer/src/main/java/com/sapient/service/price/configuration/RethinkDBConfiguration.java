package com.sapient.service.price.configuration;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RethinkDBConfiguration {
    @Value(value = "${rethinkdb.host:127.0.0.1}")
    private String host;

    @Value(value = "${rethinkdb.port:28015}")
    private int port;

    @Value(value = "${rethinkdb.db}")
    private String db;

    private static final RethinkDB r = RethinkDB.r;

    @Bean
    public RethinkDB rethinkDB() {
        return r;
    }

    @Bean
    public Connection rethinkDBConnection() {
        return r.connection()
                .hostname(host)
                .port(port)
                .db(db)
                .connect();
    }
}
