package com.nike.wms.vo;

import java.util.Map;

/**
 * this class defines all the data elements for Login
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
public class LoginVO {
	private  String id;
	private String userName;;
	private Map<String, String> role;
	private String orgName;
	private String orgAddress;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the role
	 */
	public Map<String, String> getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(Map<String, String> role) {
		this.role = role;
	}
	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return orgName;
	}
	/**
	 * @param orgName the orgName to set
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	/**
	 * @return the orgAddress
	 */
	public String getOrgAddress() {
		return orgAddress;
	}
	/**
	 * @param orgAddress the orgAddress to set
	 */
	public void setOrgAddress(String orgAddress) {
		this.orgAddress = orgAddress;
	}


}
