package cc.touchuan.jbus.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceProxyManager {

	// sessionId, DeviceProxy
	static Map<String, DeviceProxy> _deviceList = new ConcurrentHashMap<String, DeviceProxy>();
	
	public static DeviceProxy findProxy(String sessionId) {
		
		return _deviceList.get(sessionId);
	}

	public static void closeProxy(String sessionId) {
		_deviceList.remove(sessionId);
	}
	
	public static DeviceProxy createProxy(String sessionId, String host, int port) {
		DeviceProxy proxy = new DeviceProxy(sessionId, host, port);
		
		_deviceList.put(sessionId, proxy);
	
		
		return proxy;
	}
	

	
	
	
	
}
