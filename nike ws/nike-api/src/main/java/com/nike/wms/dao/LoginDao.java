package com.nike.wms.dao;

import java.sql.SQLException;
import com.nike.wms.vo.LoginVO;

/**
 * LoginDao interface - declares all the methods for login functionality. 
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
public interface LoginDao {
	public LoginVO getLogin(String userName,String password) throws SQLException, ClassNotFoundException ;
}
