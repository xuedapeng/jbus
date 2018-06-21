package cc.touchuan.jbus.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerProxyManager {


	// sessionId, ControllerProxy
	static Map<String, ControllerProxy> _controllerList = new ConcurrentHashMap<String, ControllerProxy>();
	
	public static ControllerProxy findProxy(String sessionId) {
		
		return _controllerList.get(sessionId);
	}
	
	public static void closeProxy(String sessionId) {
		ControllerProxy cproxy = _controllerList.get(sessionId);
		if (cproxy != null) {
			cproxy.close();
		}
		
		_controllerList.remove(sessionId);
	}
	
	public static ControllerProxy createProxy(String sessionId, String deviceId) {
		
		ControllerProxy cproxy = new ControllerProxy(sessionId, deviceId);

		
		_controllerList.put(sessionId, cproxy);
		
		return cproxy;
	}
	
	
	
	
}
