package cc.touchuan.jbus.auth;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.touchuan.jbus.auth.db.ConnectionPool;
import cc.touchuan.jbus.auth.db.DeviceAliasDao;
import cc.touchuan.jbus.auth.db.DeviceAliasEntity;
import cc.touchuan.jbus.auth.db.DeviceDao;
import cc.touchuan.jbus.auth.db.DeviceEntity;
import cc.touchuan.jbus.auth.db.DeviceTypeDao;
import cc.touchuan.jbus.auth.db.DeviceTypeEntity;
import cc.touchuan.jbus.common.helper.ByteHelper;
import cc.touchuan.jbus.common.helper.HexHelper;
import cc.touchuan.jbus.common.helper.JsonHelper;
import io.netty.util.CharsetUtil;

public class DeviceAuthProxy {

	// id/token 验证
	public static boolean checkToken(String deviceId, String accessToken) {
		
		Connection conn = ConnectionPool.getInstance();
		if (conn == null) {
			return false;
		}
		
		DeviceEntity device = new DeviceDao(conn).findBySn(deviceId);
		if (device == null) {
			return false;
		}
		
		if (device.getPassword().equals(accessToken)) {
			return true;
		}
		
		return false;
	}
	
	public static byte[] makeRegInfo(byte[] regBytes, DeviceTypeEntity deviceType) {
		
		String aliasRule = deviceType.getAliasRule();

		List<Object> arList = JsonHelper.json2list(aliasRule);
		if (arList == null ||  arList.size() < 2) {
			return null;
		}
		
		int pos = ((Double)arList.get(0)).intValue();
		if (pos >= regBytes.length) {
			return null;
		}

		int len = ((Double)arList.get(1)).intValue();
		int endPos = pos+len;

		if (endPos > regBytes.length) {
			return null;
		}
		
		byte[] aliasBytes = Arrays.copyOfRange(regBytes, pos, endPos);
		
		String alias = new String(aliasBytes, CharsetUtil.UTF_8);
		
		DeviceEntity device = findDeviceByAlias(alias);
		if (device == null) {
			
			// 尝试二进制字符串 0x6409100472
			alias = "0x"+ HexHelper.bytesToHexString(aliasBytes,"");
			device = findDeviceByAlias(alias);
			
			if (device == null) {
				return null;
			}
		}
		
		String deviceSn = device.getDeviceSn();
		String token = device.getPassword();
		
		String regStr = String.format("REG%s,%s,%d;", deviceSn, token, deviceType.getHeartbeat());
		
		return regStr.getBytes(CharsetUtil.UTF_8);
		
	}
	
	// 来源IP验证
	public static boolean checkFromAddr(String host, int port) {
		
		return true;
	}
	
	// 匹配设备类型
	public static DeviceTypeEntity findDeviceType(byte[] regBytes) {
		
		List<DeviceTypeEntity> types = new DeviceTypeDao(ConnectionPool.getInstance()).findAll();
		
		if (types == null || types.size() == 0) {
			return null;
		}
		
		for (DeviceTypeEntity type: types) {
			String fingerprint = type.getFingerprint();
			if (checkFingerprint(fingerprint, regBytes)) {
				return type;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static boolean checkFingerprint(String fingerprint, byte[] regBytes) {
		
		List<Object> fpList = JsonHelper.json2list(fingerprint);
		if (fpList == null ||  fpList.size() == 0) {
			return false;
		}
		

		boolean hit = true;
		for(Object fp: fpList) {
			int pos = ((List<Double>)fp).get(0).intValue();
			if (pos < 0) {
				pos = regBytes.length + pos;
			}
			if (pos >= regBytes.length) {
				hit = false;
				break;
			}

			int val = ((List<Double>)fp).get(1).intValue();
			int regVal = ByteHelper.toUnsignedInt(regBytes[pos]);
			if (regVal != val) {
				hit = false;
				break;
			}
		}
		
		return hit;
	}
	
	private static DeviceEntity findDeviceByAlias(String deviceAlias) {
		
		DeviceAliasEntity aliasEntity = 
				new DeviceAliasDao(ConnectionPool.getInstance()).findByAlias(deviceAlias);
		
		if (aliasEntity == null) {
			return null;
		}
		
		DeviceEntity device = new DeviceDao(ConnectionPool.getInstance()).findBySn(aliasEntity.getDeviceSn());
		
		return device;
		
	}
	
}
