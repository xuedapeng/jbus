package cc.touchuan.jbus.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cc.touchuan.jbus.common.constant.Keys;
import cc.touchuan.jbus.common.exception.JbusException;
import cc.touchuan.jbus.common.helper.CryptoHelper;
import cc.touchuan.jbus.proxy.ControllerProxyManager;
import cc.touchuan.jbus.proxy.DeviceProxyManager;
import io.netty.channel.Channel;

public class SessionManager {

	static Map<String, Session> _sessionList = new ConcurrentHashMap<String, Session>();
	
	
	public static Session findSession(String sessionId) {
		
		return _sessionList.get(sessionId);
	}
	
	public static void closeSession(String sessionId) {
		
		_sessionList.remove(sessionId);
		
		DeviceProxyManager.closeProxy(sessionId);
		ControllerProxyManager.closeProxy(sessionId);
	}
	
	public static Session createSession(Channel channel) {
		
		// channel 是否已经创建session
		String sessionId = channel.attr(Keys.SESSION_ID_KEY).get();
		if (sessionId != null) {
			throw new JbusException("Channel already bound with session.");
		} 
		
		// 创建session
		sessionId = makeSessionId();
		Session session = new Session();
		session.setSessionId(sessionId);
		session.setChannel(channel);
		_sessionList.put(sessionId, session);
		
		// channel添加sessionId
		channel.attr(Keys.SESSION_ID_KEY).set(sessionId);
		
		return session;
		
		
	}
	
	
	private static String makeSessionId() {
		
		return CryptoHelper.genUUID();
	}
	
}
