package cc.touchuan.jbus.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.constant.Keys;
import cc.touchuan.jbus.common.exception.JbusException;
import cc.touchuan.jbus.common.helper.CryptoHelper;
import cc.touchuan.jbus.proxy.MqttProxy;
import io.netty.channel.Channel;

public class SessionManager {

	static Logger logger = Logger.getLogger(SessionManager.class);

	static final long IDLE_SLEEP = 10*1000;// 休眠10秒；
	
	// sessionId, session
	static Map<String, Session> _sessionMap = new ConcurrentHashMap<String, Session>();
	
	// deviceId, List<session>
	static Map<String, List<Session>> _device2SessionMap = new ConcurrentHashMap<String, List<Session>>();
	

	public static void initialize() {
		checkLostTask();
	}
	
	public static Session findBySessionId(String sessionId) {
		
		return _sessionMap.get(sessionId);
	}
	
	public static List<Session> findByDeviceId(String deviceId) {
		
		return _device2SessionMap.get(deviceId);
	}
	
	public static void closeSession(String sessionId) {
		
		Session removed = _sessionMap.remove(sessionId);
		
		String deviceId = removed.getDeviceProxy().getDeviceId();
		List<Session> sessionList = _device2SessionMap.get(deviceId);
		sessionList.remove(removed);
		
		if (sessionList.isEmpty()) {
			_device2SessionMap.remove(deviceId);
			
			// 取消订阅
			MqttProxy.unSubscribe(deviceId);
		}
		
		EventManager.publish(
				new Event(removed, EventManager.EVENT_OFF));
		
	}
	
	public static Session createSession(Channel channel, String host, int port) {
		
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
		session.setHost(host);
		session.setPort(port);
		session.setStartTime(new Date());
		
		_sessionMap.put(sessionId, session);
		
		// channel添加sessionId
		channel.attr(Keys.SESSION_ID_KEY).set(sessionId);
		
		return session;
	}
	
	// session里面的成员发生变化后，触发此事件，更新_device2SessionMap
	public static void deviceIdUpdateEvent(String sessionId, String oldDeviceId, String newDeviceId) {
		Session session = findBySessionId(sessionId);
		
		List<Session> sessionList = _device2SessionMap.get(oldDeviceId);
		if (sessionList != null) {
			sessionList.remove(session);
			if (sessionList.isEmpty()) {
				_device2SessionMap.remove(oldDeviceId);
			}
		}
		
		deviceIdCreateEvent(sessionId, newDeviceId);
	}
	
	public static void deviceIdCreateEvent(String sessionId, String deviceId) {

		Session session = findBySessionId(sessionId);

		if (!_device2SessionMap.containsKey(deviceId)) {
			_device2SessionMap.put(deviceId, new ArrayList<Session>());
		}
		
		
		List<Session> sessionList = _device2SessionMap.get(deviceId);
		sessionList.add(session);
		
	}
	
	
	private static String makeSessionId() {
		
		return CryptoHelper.genUUID();
	}
	

	// 断开死连接
	private static void checkLostTask() {
		
		new Thread() {

			@Override  
			public void run() {
				
				while(true) {
					try {
						List<Session> inactiveSessionList = new ArrayList<Session>();
						_sessionMap.forEach((K,V)->{
							if (!V.getChannel().isActive()) {
								inactiveSessionList.add(V);
							}
						});

						logger.info("_sessionMap.size=" + _sessionMap.size());
						logger.info("inactiveSessionList.size=" + inactiveSessionList.size());
						if (inactiveSessionList.size() > 0) {
							inactiveSessionList.forEach((E)->{
								closeSession(E.getSessionId());
								logger.info("closeSession:" + E.getSessionId());
							});
						}
						
						try {
							sleep(IDLE_SLEEP);
						} catch (InterruptedException e) {
							logger.error("", e);
						}
					} catch(Exception e) {
						logger.error("", e);
					}
				}
			}
			
		}.start();
	}
	

	/*
	 * 提供给json-Rpc的接口 
	 */
	
	public static class JsonRpc {

		
		public synchronized static List<Session> getSessionList() {
			return _sessionMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
		}
		
		public static List<String> getDeviceList() {
			return _device2SessionMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
		}
		
		// Map<deviceId, on/off>
		public static Map<String, String> getOnlineStateOfDevices(List<String> deviceIds) {
			return deviceIds.stream().collect(
					Collectors.toMap(
							Function.identity(), 
							(deviceId)->_device2SessionMap.containsKey(deviceId)?"on":"off"));
			
		}
		
	}
	
}
