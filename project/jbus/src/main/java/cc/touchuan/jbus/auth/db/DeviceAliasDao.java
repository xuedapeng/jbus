package cc.touchuan.jbus.auth.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

public class DeviceAliasDao extends BaseDao {

	public DeviceAliasDao(Connection _conn) {
		super(_conn);
	}
	
	public DeviceAliasEntity findByAlias(String deviceAlias) {

		try {
	        //创建SQL执行工具   
	        QueryRunner qRunner = new QueryRunner();  
	        
			String sql = "select * from t_device_alias ";
			sql += " where deviceAlias = ?";
			sql += " order by id desc limit 1 ";

			BeanHandler<DeviceAliasEntity> bh = new BeanHandler<DeviceAliasEntity>(DeviceAliasEntity.class);
			 
			DeviceAliasEntity bean = (DeviceAliasEntity) qRunner.query(conn, sql, bh, deviceAlias);
	        
	        return bean;
	        
		} catch (SQLException e) {
			
			LOG.error("", e);
			return null;
		} 
	}

}
