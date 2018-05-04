package com.nike.wms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import com.nike.wms.util.NikeUtil;
import com.nike.wms.vo.LoginVO;

/**
 * LoginDaoImpl class - This is an implementation class for LoginDao interface which has definitions of all Login functionalities.
 * @author Cognizant Blockchain Team
 * @version 1.0
 */

@Repository
public class LoginDaoImpl implements LoginDao {
	@Autowired
	private NikeUtil nikeUtil;
	@Value(value = "${node.rpc.hostport}")
	private String nodeRpcHostAndPort;
	@Value(value = "${node.db.connection}")
	private String nodeDbConnection;
	@PersistenceContext	

	/**
	 * Method for login user based on given credentials
	 */
	@Override
	//@Transactional
	public LoginVO getLogin(String userName, String password) throws SQLException, ClassNotFoundException {
		Statement loginStatement = null;
		Statement loginStatement2 = null;

		Connection dbConn = null;
		ResultSet rs = null;
		ResultSet rs1 = null;

		LoginVO loginVO = new LoginVO();
		try {
			
			dbConn = nikeUtil.getDBConnection();

			Map<String, String> roleMap = new HashMap<>();

			String sqlCreate = "SELECT * FROM APP_LOGIN WHERE ID='" + userName.toLowerCase() + "' AND PASSWORD='"
					+ password + "'";
			loginStatement = dbConn.createStatement();
			rs = loginStatement.executeQuery(sqlCreate);
			if (rs != null && rs.next()) {
				String sqlDetails = "SELECT * FROM LOGIN_DETAILS WHERE ID='" + userName.toLowerCase() + "'";
				loginStatement2 = dbConn.createStatement();
				rs1 = loginStatement2.executeQuery(sqlDetails);
				if (rs1 != null && rs1.next()) {
					loginVO.setId(rs1.getString(1));
					loginVO.setUserName(rs1.getString(2));
					roleMap.put("roleName", rs1.getString(5));
					loginVO.setRole(roleMap);
					loginVO.setOrgName(rs1.getString(3));
					loginVO.setOrgAddress(rs1.getString(4));

				}
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			// finally block used to close resources
			try {
				if (rs != null) {
					rs.close();
				}
				if (rs1 != null) {
					rs1.close();
				}

				if (loginStatement != null) {
					loginStatement.close();
				}
				if (loginStatement2 != null) {
					loginStatement2.close();
				}
				if (dbConn != null) {
					dbConn.close();
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw e;
			}
		}
		return loginVO;
	}
}
