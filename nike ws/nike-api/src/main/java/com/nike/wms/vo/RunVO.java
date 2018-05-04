package com.nike.wms.vo;

/**
 * This is a class which defines all the elements of Run object
 * @author Cognizant Blockchain Team
 * @version 1.0
 */

public class RunVO {
	private String runID;
    private String responseID;
    private String skuID;
    private String runnerID;
    private String runnerName;
    private String storeName;
    private String runnerPosition;
    private String statusChangeTimeStamp;
	/**
	 * @return the runID
	 */
	public String getRunID() {
		return runID;
	}
	/**
	 * @param runID the runID to set
	 */
	public void setRunID(String runID) {
		this.runID = runID;
	}
	/**
	 * @return the responseID
	 */
	public String getResponseID() {
		return responseID;
	}
	/**
	 * @param responseID the responseID to set
	 */
	public void setResponseID(String responseID) {
		this.responseID = responseID;
	}
	/**
	 * @return the skuID
	 */
	public String getSkuID() {
		return skuID;
	}
	/**
	 * @param skuID the skuID to set
	 */
	public void setSkuID(String skuID) {
		this.skuID = skuID;
	}
	/**
	 * @return the runnerID
	 */
	public String getRunnerID() {
		return runnerID;
	}
	/**
	 * @param runnerID the runnerID to set
	 */
	public void setRunnerID(String runnerID) {
		this.runnerID = runnerID;
	}
	/**
	 * @return the runnerName
	 */
	public String getRunnerName() {
		return runnerName;
	}
	/**
	 * @param runnerName the runnerName to set
	 */
	public void setRunnerName(String runnerName) {
		this.runnerName = runnerName;
	}
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
	 * @return the runnerPosition
	 */
	public String getRunnerPosition() {
		return runnerPosition;
	}
	/**
	 * @param runnerPosition the runnerPosition to set
	 */
	public void setRunnerPosition(String runnerPosition) {
		this.runnerPosition = runnerPosition;
	}
	/**
	 * @return the statusChangeTimeStamp
	 */
	public String getStatusChangeTimeStamp() {
		return statusChangeTimeStamp;
	}
	/**
	 * @param statusChangeTimeStamp the statusChangeTimeStamp to set
	 */
	public void setStatusChangeTimeStamp(String statusChangeTimeStamp) {
		this.statusChangeTimeStamp = statusChangeTimeStamp;
	}
}
