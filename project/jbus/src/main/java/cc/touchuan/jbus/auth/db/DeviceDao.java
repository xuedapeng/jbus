package cc.touchuan.jbus.auth.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

public class DeviceDao extends BaseDao {

	public DeviceDao(Connection _conn) {
		super(_conn);
	}
	
	public DeviceEntity findBySn(String deviceSn) {

		try {
	        //创建SQL执行工具   
	        QueryRunner qRunner = new QueryRunner();  
	        
			String sql = "select * from v_jbus_device ";
			sql += " where deviceSn = ?";
			sql += " order by id asc limit 1 ";

			BeanHandler<DeviceEntity> bh = new BeanHandler<DeviceEntity>(DeviceEntity.class);
			 
			DeviceEntity bean = (DeviceEntity) qRunner.query(conn, sql, bh, deviceSn);
	        
	        return bean;
	        
		} catch (SQLException e) {
			
			LOG.error("", e);
			return null;
		} 
	}

}
