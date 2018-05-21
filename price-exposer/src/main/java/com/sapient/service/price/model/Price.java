package com.sapient.service.price.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Created by hgupta.
 */
@JsonIgnoreProperties(value = "id")
public class Price {
    private String skuId;
    private Long price;
    private String currency;

    public Price() {

    }

    public Price(Long price, String skuId) {
        this.skuId = skuId;
        this.price = price;
    }
    
    public Price(String skuId) {
        this.skuId = skuId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

    public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}



}
