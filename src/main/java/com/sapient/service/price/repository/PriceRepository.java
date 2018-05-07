package com.sapient.service.price.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sapient.service.price.model.Price;

/**
 * Created by hgupta.
 */
@Repository
public interface PriceRepository extends ReactiveMongoRepository<Price, String> {

}
