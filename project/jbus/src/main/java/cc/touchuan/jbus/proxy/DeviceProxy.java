package cc.touchuan.jbus.proxy;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.helper.HexHelper;
import cc.touchuan.jbus.session.Session;
import cc.touchuan.jbus.session.SessionManager;


public class DeviceProxy {

	static Logger logger = Logger.getLogger(DeviceProxy.class);
	
	private String sessionId;
	private String deviceId;
	

	public DeviceProxy(String sessionId, String host, int port) {
		this.sessionId = sessionId;
		this.deviceId = makeDeviceId(host, port);

		// device create event
		SessionManager.deviceIdCreateEvent(sessionId, deviceId);
		
		// 创建控制器
		this.createOrUpdateController();
	}

	public void updateDeviceId(String deviceId) {
		
		String oldDeviceId = this.deviceId;
		this.deviceId = deviceId;

		// device create event
		SessionManager.deviceIdUpdateEvent(sessionId, oldDeviceId, deviceId);

		// 更新控制器
		this.createOrUpdateController();
	}
	
	private void createOrUpdateController() {
		
		ControllerProxy cproxy = ControllerProxyManager.findProxy(sessionId);
		if (cproxy == null) {
			cproxy = ControllerProxyManager.createProxy(sessionId, deviceId);
		} else {
			cproxy.updateDeviceId(deviceId);
		}
		
		
		logger.info("connectCtrl:");
	}
	

	public void sendData(byte[] data) {

		Session session = SessionManager.findBySessionId(this.sessionId);
		session.getControllerProxy().recieveData(data);
		
		logger.info("sendData:" + HexHelper.bytesToHexString(data));
	}
	
	public void recieveCommand(byte[] command) {

		Session session = SessionManager.findBySessionId(this.sessionId);
		session.getChannel().writeAndFlush(command);
	}

	public String getDeviceId() {
		return deviceId;
	}

	private static String makeDeviceId(String host, int port) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(host.replace(".", "_"));
		sb.append("__");
		sb.append(port);
		
		return sb.toString();
	}
	
}
