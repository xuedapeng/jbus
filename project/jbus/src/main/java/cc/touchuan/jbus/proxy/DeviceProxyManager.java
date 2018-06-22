package cc.touchuan.jbus.proxy;

import cc.touchuan.jbus.session.SessionManager;

public class DeviceProxyManager {

	// sessionId, DeviceProxy
//	static Map<String, DeviceProxy> _deviceMap = new ConcurrentHashMap<String, DeviceProxy>();
	
	public static DeviceProxy findProxy(String sessionId) {
		
		return SessionManager.findBySessionId(sessionId).getDeviceProxy();
	}

	
	public static DeviceProxy createProxy(String sessionId, String host, int port) {
		DeviceProxy proxy = new DeviceProxy(sessionId, host, port);
		
		SessionManager.findBySessionId(sessionId).setDeviceProxy(proxy);	
		return proxy;
	}
	

	
	
	
	
}
