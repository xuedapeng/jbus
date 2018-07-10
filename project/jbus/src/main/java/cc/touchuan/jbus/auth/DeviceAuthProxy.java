package cc.touchuan.jbus.auth;

public class DeviceAuthProxy {

	// id/token 验证
	public static boolean checkToken(String deviceId, String accessToken) {
		
		return true;
	}
	
	// 来源IP验证
	public static boolean checkFromAddr(String host, int port) {
		
		return true;
	}
}
