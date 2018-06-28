package cc.touchuan.jbus.proxy;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.helper.HexHelper;
import cc.touchuan.jbus.session.Session;
import cc.touchuan.jbus.session.SessionManager;

public class ControllerProxy {

	static Logger logger = Logger.getLogger(ControllerProxy.class);
	
	private String deviceId;
	private String sessionId;


	public ControllerProxy(String sessionId, String deviceId) {
		this.sessionId = sessionId;
		this.deviceId = deviceId;
		
		MqttProxy.subscribe(deviceId);
		
	}

	public void updateDeviceId(String deviceId) {

		String oldDevice = this.deviceId;
		
		this.deviceId = deviceId;

		MqttProxy.unSubscribe(oldDevice);
		MqttProxy.subscribe(this.deviceId);

		
	}
	

	// 向device下发命令
	public void sendCommand(byte[] command) {

		Session session = SessionManager.findBySessionId(this.sessionId);
		session.getDeviceProxy().recieveCommand(command);
		
		logger.info("sendCommand:" + HexHelper.bytesToHexString(command));

	}
	
	public void recieveData(byte[] data) {

		logger.info("recieveData:" + HexHelper.bytesToHexString(data));
		
		MqttProxy.publish(deviceId, data);

	}

	
	
	
	
	
}
