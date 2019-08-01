package cc.touchuan.jbus.auth.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

public class BaseDao {

	static Logger LOG = Logger.getLogger(BaseDao.class);
	
	protected Connection conn;
	public BaseDao(Connection _conn) {
		this.conn = _conn;
	}
	
	public static final String SQL_GET_ID = "select @@identity";
	

	@SuppressWarnings("rawtypes")
	protected List assertNotNullList(List list) {
		if (list == null) {
			return new ArrayList();
		} else {
			return list;
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected Object getFirstItemOfList(List list) {
		if (list==null||list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}
	

	public static boolean delete(String tableName, Long id, Connection conn) {
		try {
	        //创建SQL执行工具   
	        QueryRunner qRunner = new QueryRunner();  
	        
			String sql = "delete from " + tableName ;
			sql += " where id=? ";
				  
			qRunner.update(conn, sql, id);
	        
			return true;
		} catch (SQLException e) {

			LOG.error("", e);
			return false;
		} 
	
	}
}
