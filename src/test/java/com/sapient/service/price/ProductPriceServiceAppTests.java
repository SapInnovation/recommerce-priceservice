package com.sapient.service.price;

import com.sapient.service.price.model.Price;
import com.sapient.service.price.repository.PriceRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@SpringJUnitConfig(ProductPriceServiceApp.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductPriceServiceAppTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    PriceRepository priceRepository;

    @Test
    void testCreatePrice() {
        Price price = new Price(10.0, "This is a Test price");

        webTestClient.post().uri("/price")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(price), Price.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.productId").isNotEmpty()
                .jsonPath("$.currency").isEqualTo("GBP");
    }

    @Test
    void testGetAllPrices() {
        webTestClient.get().uri("/price")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Price.class);
    }

    @Test
    void testGetSinglePrice() {
        Price price = priceRepository.save(new Price(10.0, "123")).block();

        webTestClient.get()
                .uri("/price/{productId}", Collections.singletonMap("productId", price.getProductId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response ->
                        Assertions.assertThat(response.getResponseBody()).isNotNull());
    }

    @Test
    void testUpdatePrice() {
        Price price = priceRepository.save(new Price(11.0, "123")).block();

        Price newPriceData = new Price(11.0, "123");

        webTestClient.put()
                .uri("/price/{productId}", Collections.singletonMap("productId", price.getProductId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(newPriceData), Price.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.price").isEqualTo(11.0);
    }

    @Test
    void testDeletePrice() {
        Price price = priceRepository.save(new Price("123")).block();

        webTestClient.delete()
                .uri("/Price/{productId}", Collections.singletonMap("productId", price.getProductId()))
                .exchange()
                .expectStatus().isOk();
    }
}
