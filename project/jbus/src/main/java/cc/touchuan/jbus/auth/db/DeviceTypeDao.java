package cc.touchuan.jbus.auth.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

public class DeviceTypeDao extends BaseDao {

	public DeviceTypeDao(Connection _conn) {
		super(_conn);
	}
	
	public List<DeviceTypeEntity> findAll() {

		try {
	        //创建SQL执行工具   
	        QueryRunner qRunner = new QueryRunner();  
	        
			String sql = "select * from t_device_type ";

			BeanListHandler<DeviceTypeEntity> bh = new BeanListHandler<DeviceTypeEntity>(DeviceTypeEntity.class);
			 
			List<DeviceTypeEntity> beanList = (List<DeviceTypeEntity>) qRunner.query(conn, sql, bh);
	        
	        return beanList;
	        
		} catch (SQLException e) {
			
			LOG.error("", e);
			return null;
		} 
	}

}
