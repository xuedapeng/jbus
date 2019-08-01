package cc.touchuan.jbus.proxy;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.helper.ByteHelper;
import cc.touchuan.jbus.common.helper.HexHelper;
import cc.touchuan.jbus.session.Session;
import cc.touchuan.jbus.session.Session.PROT_TYPE;
import cc.touchuan.jbus.session.SessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

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

		logger.info("sendData:" + HexHelper.bytesToHexString(data));
		
		Session session = SessionManager.findBySessionId(this.sessionId);
		session.getControllerProxy().recieveData(data);
		
		Channel channel = session.getChannel();
		logger.info("channel.isWritable=" + channel.isWritable());
		
	}
	
	public void recieveCommand(byte[] command) {

		logger.info("recieveCommand:" + HexHelper.bytesToHexString(command));
		
		Session session = SessionManager.findBySessionId(this.sessionId);
		Channel channel = session.getChannel();
		
		logger.info("channel.isWritable=" + channel.isWritable());
		
		ByteBuf bb = ByteHelper.bytes2bb(command);
		if (PROT_TYPE.WS_BIN.equals(session.getProtType())) {

			BinaryWebSocketFrame msg = new BinaryWebSocketFrame(bb);
			channel.writeAndFlush(msg);
//			logger.info("BinaryWebSocketFrame=" + msg.content()); // 引用计数异常
			
		} else if (PROT_TYPE.WS_TXT.equals(session.getProtType())) {
			
			TextWebSocketFrame msg = new TextWebSocketFrame(bb);
			channel.writeAndFlush(msg);
//			logger.info("TextWebSocketFrame=" + msg.content());  // 引用计数异常
			
		} else {
			
			channel.writeAndFlush(bb)
				.addListener(
					ChannelFutureListener.CLOSE_ON_FAILURE);
			
		}
		
		
	}

	public String getDeviceId() {
		return deviceId;
	}

	private static String makeDeviceId(String host, int port) {
		
		StringBuffer sb = new StringBuffer();

		sb.append("NOSN/");
		sb.append(host.replace(".", "_"));
		sb.append("__");
		sb.append(port);
		
		return sb.toString();
	}
	
}
