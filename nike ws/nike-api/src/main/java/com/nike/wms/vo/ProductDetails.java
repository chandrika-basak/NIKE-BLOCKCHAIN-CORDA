package com.nike.wms.vo;

/**
 * This is a class which defines all Product related data elements.
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
public class ProductDetails {
	private String itemNo;
	private String itemName;;
	private String itemDesc;
	private String longItemDesc;
	private String curUnitPrice;
	private String mfgSuggestPrc;
	/**
	 * @return the itemNo
	 */
	public String getItemNo() {
		return itemNo;
	}
	/**
	 * @param itemNo the itemNo to set
	 */
	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}
	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}
	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	/**
	 * @return the itemDesc
	 */
	public String getItemDesc() {
		return itemDesc;
	}
	/**
	 * @param itemDesc the itemDesc to set
	 */
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
	/**
	 * @return the longItemDesc
	 */
	public String getLongItemDesc() {
		return longItemDesc;
	}
	/**
	 * @param longItemDesc the longItemDesc to set
	 */
	public void setLongItemDesc(String longItemDesc) {
		this.longItemDesc = longItemDesc;
	}
	/**
	 * @return the curUnitPrice
	 */
	public String getCurUnitPrice() {
		return curUnitPrice;
	}
	/**
	 * @param curUnitPrice the curUnitPrice to set
	 */
	public void setCurUnitPrice(String curUnitPrice) {
		this.curUnitPrice = curUnitPrice;
	}
	/**
	 * @return the mfgSuggestPrc
	 */
	public String getMfgSuggestPrc() {
		return mfgSuggestPrc;
	}
	/**
	 * @param mfgSuggestPrc the mfgSuggestPrc to set
	 */
	public void setMfgSuggestPrc(String mfgSuggestPrc) {
		this.mfgSuggestPrc = mfgSuggestPrc;
	}

	

}
