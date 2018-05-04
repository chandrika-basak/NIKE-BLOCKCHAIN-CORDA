package com.nike.wms.service;

import com.nike.wms.vo.LoginVO;
import com.nike.wms.vo.UserVO;

/**
 * Interface for Login Service defining login method
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
public interface LoginService {
 public LoginVO login(UserVO userVO); 
}
