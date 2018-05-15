package com.sapient.service.price.configuration;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RethinkDBConfiguration {
    private static final RethinkDB r = RethinkDB.r;

    @Bean
    public RethinkDB rethinkDB() {
        return r;
    }

    @Bean
    public Connection rethinkDBConnection(final @Value("${custom.rethinkdb.host}") String hostname,
                                          final @Value("${custom.rethinkdb.port}") int port) {
        return r.connection()
                .hostname(hostname)
                .port(port)
                .connect();
    }
}
