package com.nike.wms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.nike.wms.dao.LoginDao;
import com.nike.wms.vo.LoginVO;
import com.nike.wms.vo.UserVO;

/**
 * Implementation class for LoginService interface, which defines methods for all login functionalities.
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
@Service
@EnableScheduling
public class LogniServiceImpls implements LoginService {

	@Value(value = "${node.rpc.hostport}")
	private String nodeRpcHostAndPort;
	@Value(value = "${node.db.connection}")
	private String nodeDbConnection;
	@Autowired
	private LoginDao loginDao;

	/**
	 * 
	 */
	@Override
	public LoginVO login(UserVO userVO) {
		LoginVO loginVO = new LoginVO();
		try {
			loginVO = loginDao.getLogin(userVO.getUserId(), userVO.getPassword());

		} catch (Exception e) {
			e.printStackTrace();

		}
		return loginVO;
	}
}
