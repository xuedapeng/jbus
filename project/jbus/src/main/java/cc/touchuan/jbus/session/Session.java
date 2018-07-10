package cc.touchuan.jbus.session;

import java.util.Date;

import cc.touchuan.jbus.proxy.ControllerProxy;
import cc.touchuan.jbus.proxy.DeviceProxy;
import io.netty.channel.Channel;

public class Session {

	private String sessionId;
	private Channel channel;
	private DeviceProxy deviceProxy;
	private ControllerProxy controllerProxy;
	private String host;
	private int port;
	private Date startTime;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public DeviceProxy getDeviceProxy() {
		return deviceProxy;
	}

	public void setDeviceProxy(DeviceProxy deviceProxy) {
		this.deviceProxy = deviceProxy;
	}

	public ControllerProxy getControllerProxy() {
		return controllerProxy;
	}

	public void setControllerProxy(ControllerProxy controllerProxy) {
		this.controllerProxy = controllerProxy;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	
	
}
