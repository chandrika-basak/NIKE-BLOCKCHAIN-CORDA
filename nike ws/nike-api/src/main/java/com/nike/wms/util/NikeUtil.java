package com.nike.wms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * This is an Util class for Nike application
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
@Component
public class NikeUtil {
	
	@Value(value = "${node.db.connection}")
	private  String nodeDbConnection;
	
	@Value(value = "${ext.db.connection}")
	private String extDbConnection;
	
	@Value(value = "${ext.username}")
	private String extUsername;

	@Value(value = "${ext.password}")
	private String extPassword;

	
	public static Timestamp dateToTimeStamp(Date date){
		long time = date.getTime();
		return new Timestamp(time);
	}
	
	public static Date stringToDate(String stringDate){
		Date convertedDate = null;
		SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");
		try {
			convertedDate =sd.parse(stringDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return convertedDate;
		
	}
	
	public Connection getDBConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.h2.Driver");
		String dbConnectionString = nodeDbConnection;
		Connection dbConn = DriverManager.getConnection(dbConnectionString, "sa", "");
		return dbConn;
	}

	public Connection getextDBConnection() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		String dbConnectionString = extDbConnection;
		Connection dbConn = DriverManager.getConnection(dbConnectionString, extUsername, extPassword);
		return dbConn;
	}

}
