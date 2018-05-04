package com.nike.wms.vo;

/**
 * This class defines all the data elements of logged-in user
 * @author Cognizant Blockchain Team
 * @version 1.0
 */

public class UserVO {

	private String userId;
	private String password;
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
