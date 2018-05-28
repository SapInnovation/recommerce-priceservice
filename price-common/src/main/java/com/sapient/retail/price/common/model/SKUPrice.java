package com.sapient.retail.price.common.model;

import javax.validation.constraints.NotNull;

/**
 * Bean with fields mapped to RETHINK DB collection and its fields as mentioned
 */

/**
 * @author hitgupta
 *
 */
public class SKUPrice {
	@NotNull
	private String skuId;
	@NotNull
	private Double price;

	/**
	 * @return the skuId
	 */
	public String getSkuId() {
		return skuId;
	}

	/**
	 * @param skuId
	 *            the skuId to set
	 */
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
	}
}
