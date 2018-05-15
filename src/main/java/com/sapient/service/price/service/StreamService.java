package com.sapient.service.price.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import com.sapient.service.price.model.PriceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;

@Service
public class StreamService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final RethinkDB rethinkDB;
    private final Connection connection;

    public StreamService(final RethinkDB rethinkDB,
                         final Connection connection) {
        this.rethinkDB = rethinkDB;
        this.connection = connection;
    }

    @SuppressWarnings("unchecked")
    public Flux<PriceStream> registerStream(final long skuId) {
        LOGGER.info("Registering RethinkDB Streams for skuId: " + skuId);
        return Flux.create(stream ->
                Cursor.class.cast(rethinkDB
                        .table("stream")
                        .filter(doc -> doc.getField("skuId").eq(skuId))
                        .changes()
                        .run(connection))
                        .forEach(priceUpdate -> stream.next(
                                (new ObjectMapper())
                                        .convertValue(HashMap.class.cast(
                                                Cursor.class.cast(rethinkDB
                                                        .table("stream")
                                                        .filter(doc -> doc.getField("skuId").eq(skuId))
                                                        .run(connection)).toList().get(0)),
                                                PriceStream.class))));
    }

    public void updatePrice(final PriceStream priceStream) {
        HashMap<String, Long> updatedMap = rethinkDB.table("stream")
                .filter(doc -> doc.getField("skuId")
                        .eq(priceStream.getSkuId()))
                .update(priceStream)
                .run(connection);
        if (0 >= (updatedMap.get("replaced")
                + updatedMap.get("unchanged"))) {
            rethinkDB.table("stream")
                    .insert(priceStream)
                    .run(connection);
            LOGGER.info("Price record added, skuId:" + priceStream.getSkuId());
        } else {
            LOGGER.info("Price record updated, skuId:" + priceStream.getSkuId());
        }
    }
}
