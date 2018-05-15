package com.sapient.service.price.rest.controller;

import com.sapient.service.price.model.PriceStream;
import com.sapient.service.price.service.StreamService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class StreamController {
    private StreamService streamService;

    public StreamController(final StreamService streamService) {
        this.streamService = streamService;
    }

    @PostMapping(value = "/stream/createPrice")
    public Mono<PriceStream> createStock(@Valid @RequestBody PriceStream priceStream) {
        streamService.updatePrice(priceStream);
        return Mono.just(priceStream);
    }

    @GetMapping(value = "/stream/{skuId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PriceStream> stockStream(@PathVariable final long skuId) {
        return streamService.registerStream(skuId);
    }
}
