package cc.touchuan.jbus.proxy;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.helper.HexHelper;


public class DeviceProxy {

	static Logger logger = Logger.getLogger(DeviceProxy.class);
	
	private String sessionId;
	private String deviceId;
	private String host;
	private int port;
	

	public DeviceProxy(String sessionId, String host, int port) {
		this.sessionId = sessionId;
		this.host = host;
		this.port = port;
		this.deviceId = makeDeviceId(host, port);

		this.connectCtrl();
	}

	public void updateDeviceId(String deviceId) {
		this.deviceId = deviceId;
		this.connectCtrl();
	}
	
	protected void connectCtrl() {
		
		ControllerProxy cproxy = ControllerProxyManager.findProxy(sessionId);
		if (cproxy == null) {
			cproxy = ControllerProxyManager.createProxy(sessionId, deviceId);
		} else {
			cproxy.updateDeviceId(deviceId);
		}
		
		
		logger.info("connectCtrl:");
	}
	

	public void sendToCtrl(byte[] data) {

		logger.info("sendData:" + HexHelper.bytesToHexString(data));
		
		
		
	}
	
	public void recieveFromCtrl(byte[] data) {
		
	}
	
	
//	public String getSessionId() {
//		return sessionId;
//	}
//
//	public void setSessionId(String sessionId) {
//		this.sessionId = sessionId;
//	}
//
//	public String getDeviceId() {
//		return deviceId;
//	}
//
//
//	public String getHost() {
//		return host;
//	}
//
//	public void setHost(String host) {
//		this.host = host;
//	}
//
//	public int getPort() {
//		return port;
//	}
//
//	public void setPort(int port) {
//		this.port = port;
//	}
	

	private static String makeDeviceId(String host, int port) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(host.replace(".", "_"));
		sb.append("__");
		sb.append(port);
		
		return sb.toString();
	}
	
}
