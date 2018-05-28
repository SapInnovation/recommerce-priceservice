package com.sapient.retail.price.common.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.sapient.retail.price.common.model.SKUPrice;

public class Price {
	
	@NotNull
	@Size(min = 3, max = 20, message = "Product id should be between 3 and 20 characters")
	private String productId;
	
	@NotNull
	private String currency;
	
	private List<SKUPrice> skuPrice;
	/**
	 * @return the productID
	 */
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productID the productID to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}
	/**
	 * @return the skuPrice
	 */
	public List<SKUPrice> getSkuPrice() {
		return skuPrice;
	}
	/**
	 * @param skuPrice the skuPrice to set
	 */
	public void setSkuPrice(List<SKUPrice> skuPrice) {
		this.skuPrice = skuPrice;
	}
	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
}
