package com.sapient.service.price.model;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Created by hgupta.
 */
@Document(collection = "price")
public class Price {
    @Id
    private String productId;

    @NotBlank
    private Double price;

    @NotBlank
    private String currency;

    public Price() {

    }

    public Price(Double price, String productId) {
        this.productId = productId;
        this.price = price;
    }
    
    public Price(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

    public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}



}
