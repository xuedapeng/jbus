package cc.touchuan.jbus.proxy;

import java.util.ArrayList;
import java.util.List;

import cc.touchuan.jbus.session.Session;
import cc.touchuan.jbus.session.SessionManager;

public class ControllerProxyManager {

	// sessionId, ControllerProxy
//	static Map<String, ControllerProxy> _controllerList = new ConcurrentHashMap<String, ControllerProxy>();
	
	public static ControllerProxy findProxy(String sessionId) {
		
		return SessionManager.findBySessionId(sessionId).getControllerProxy();
	}
	
	public static List<ControllerProxy> findByDeviceId(String deviceId) {
		
		List<ControllerProxy> cproxyList = new ArrayList<ControllerProxy>();
		List<Session> sessionList = SessionManager.findByDeviceId(deviceId);
		for (Session s: sessionList) {
			ControllerProxy cp = findProxy(s.getSessionId());
			cproxyList.add(cp);
		}
		
		return cproxyList;
	}
	
	
	public static ControllerProxy createProxy(String sessionId, String deviceId) {
		
		ControllerProxy cproxy = new ControllerProxy(sessionId, deviceId);
		
		SessionManager.findBySessionId(sessionId).setControllerProxy(cproxy);
		
		return cproxy;
	}
	

	
	
}
