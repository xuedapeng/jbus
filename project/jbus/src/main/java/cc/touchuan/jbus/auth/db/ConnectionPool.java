package cc.touchuan.jbus.auth.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.conf.ZSystemConfig;

public class ConnectionPool {

	static Logger logger = Logger.getLogger(ConnectionPool.class);
	
	private static Connection _conn = null; 
	
	final static String SERVER = ZSystemConfig.getProperty("auth.mysql.server");
	final static String DBNAME = ZSystemConfig.getProperty("auth.mysql.dbname");
	final static String USER = ZSystemConfig.getProperty("auth.mysql.username");
	final static String PWD = ZSystemConfig.getProperty("auth.mysql.password");
    final static String driveClassName = "com.mysql.cj.jdbc.Driver";  
	
	public static Connection getInstance() {
		
		try {
			if (_conn != null && !_conn.isClosed()) {
				return _conn;
			}
			
			//load driver  
            Class.forName(driveClassName);  
            
            //connect db 
            _conn = DriverManager.getConnection(getUrl(), USER, PWD);  
            
        } catch (SQLException | ClassNotFoundException e) {  
			logger.error("", e);
			return null;
        } 
        
		return _conn;
	}
	
	
	private static String getUrl() {
		return String.format(
				"jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai", 
				SERVER, 
				DBNAME
				);
	}
}
