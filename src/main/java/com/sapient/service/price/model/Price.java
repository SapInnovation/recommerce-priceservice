package com.sapient.service.price.model;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Created by hgupta.
 */
@Document(collection = "pricedata.price")
public class Price {
    @Id
    private String Id;
    
    @NotBlank
    private String productid;

    @NotBlank
    private Double price;

    @NotBlank
    private String currency;

    public Price() {

    }

    public Price(Double price, String productId) {
        this.productid = productId;
        this.price = price;
    }
    
    public Price(String productId) {
        this.productid = productId;
    }

    public String getProductId() {
        return productid;
    }

    public void setProductId(String productId) {
        this.productid = productId;
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
