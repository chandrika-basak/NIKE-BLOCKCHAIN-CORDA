package com.nike.wms.vo;

/**
 * This class defines all the data elements of SkuResponse
 * @author Cognizant Blockchain Team
 * @version 1.0
 */

public class SkuResponseVO {
	private String storeName;
	private String skuId;
	private String skuAvailability;
	private String requsetId;
	private String responseId;
	/**
	 * @return the storeName
	 */
	public String getStoreName() {
		return storeName;
	}
	/**
	 * @param storeName the storeName to set
	 */
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	/**
	 * @return the skuId
	 */
	public String getSkuId() {
		return skuId;
	}
	/**
	 * @param skuId the skuId to set
	 */
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	/**
	 * @return the skuAvailability
	 */
	public String getSkuAvailability() {
		return skuAvailability;
	}
	/**
	 * @param skuAvailability the skuAvailability to set
	 */
	public void setSkuAvailability(String skuAvailability) {
		this.skuAvailability = skuAvailability;
	}
	/**
	 * @return the requsetId
	 */
	public String getRequsetId() {
		return requsetId;
	}
	/**
	 * @param requsetId the requsetId to set
	 */
	public void setRequsetId(String requsetId) {
		this.requsetId = requsetId;
	}
	/**
	 * @return the responseId
	 */
	public String getResponseId() {
		return responseId;
	}
	/**
	 * @param responseId the responseId to set
	 */
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}
}
