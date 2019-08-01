package cc.touchuan.jbus.proxy;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import cc.touchuan.jbus.common.exception.JbusException;
import cc.touchuan.jbus.common.helper.HexHelper;
import cc.touchuan.jbus.mqtt.MqttPoolManager;
import cc.touchuan.jbus.session.SessionManager;
import io.netty.util.CharsetUtil;

public class MqttProxy {

	static Logger LOG = Logger.getLogger(MqttProxy.class);
	
	public static String TOPIC_PREFIX_CMD = "TC/CMD/";
	public static String TOPIC_PREFIX_DAT = "TC/DAT/";
	public static String TOPIC_PREFIX_STS = "TC/STS/";
	
	public static void subscribe(String deviceId) {

		LOG.info(String.format("subscribe: %s", 
				encodeTopicCmd(deviceId)));
		
		try {
			MqttPoolManager.getMqttPool().getInstance(deviceId).subscribe(encodeTopicCmd(deviceId));
			
			if (MqttPoolManager.getMqttPoolLocal() != null) {
				MqttPoolManager.getMqttPoolLocal().getInstance(deviceId).subscribe(encodeTopicCmd(deviceId));
			}
			
		} catch (MqttException e) {
			LOG.error("订阅失败", e);
			throw new JbusException(e);
		}
	}
	
	public static void doAfterReconnect() {
		SessionManager.streamOfDevice2SessionMapEntrySet().forEach(E -> {
			String deviceSn = E.getKey();
			subscribe(deviceSn);
		});
	}
	
	public static void unSubscribe(String deviceId) {

		LOG.info(String.format("unSubscribe: %s", 
				encodeTopicCmd(deviceId)));
		
		try {
			MqttPoolManager.getMqttPool().getInstance(deviceId).unsubscribe(encodeTopicCmd(deviceId));
			MqttPoolManager.getMqttPool().release(deviceId);
			
			if (MqttPoolManager.getMqttPoolLocal() != null) {
				MqttPoolManager.getMqttPoolLocal().getInstance(deviceId).unsubscribe(encodeTopicCmd(deviceId));
				MqttPoolManager.getMqttPoolLocal().release(deviceId);
			}
		} catch (MqttException e) {
			LOG.error("取消订阅失败", e);
			throw new JbusException(e);
		}
	}
	
	// controller 收到device发来的数据时，调用此方法
	public static void publish(String deviceId, byte[] data) {
		
		LOG.info(String.format("publish: %s->%s", 
				encodeTopicDat(deviceId), HexHelper.bytesToHexString(data)));
		
		try {
			MqttPoolManager.getMqttPool().getInstance(deviceId).publish(
					encodeTopicDat(deviceId), 
					new MqttMessage(data));

			if (MqttPoolManager.getMqttPoolLocal() != null) {
				MqttPoolManager.getMqttPoolLocal().getInstance(deviceId).publish(
						encodeTopicDat(deviceId), 
						new MqttMessage(data));
			}
			
		} catch (MqttException e) {
			LOG.error("发布失败", e);
			throw new JbusException(e);
		}
	}

	// 发布事件(设备上下线等)
	public static void publishEvent(String topic, String data) {
		
		LOG.info(String.format("publishEvent: %s->%s", topic, data));
		
		try {
			MqttPoolManager.getMqttPool().getInstance().publish(
					topic, 
					new MqttMessage(data.getBytes(CharsetUtil.UTF_8)));

			if (MqttPoolManager.getMqttPoolLocal() != null) {
				MqttPoolManager.getMqttPoolLocal().getInstance().publish(
						topic, 
						new MqttMessage(data.getBytes(CharsetUtil.UTF_8)));
			}
			
		} catch (MqttException e) {
			LOG.error("发布失败", e);
			throw new JbusException(e);
		}
	}
	// mqtt client 收到推送时调用此方法
	public static void recieve(String topic, MqttMessage message) {
		
		LOG.info(String.format("recieve: %s->%s", 
				topic, HexHelper.bytesToHexString(message.getPayload())));
		
		String deviceId = decodeTopicCmd(topic);
		byte[] command = message.getPayload();
		
		List<ControllerProxy> cpList = ControllerProxyManager.findByDeviceId(deviceId);
		
		cpList.forEach((P)->{
			P.sendCommand(command);
		});
	}
	
	// return topic:TC/DAT/{devId}
	private static String encodeTopicDat(String deviceId) {
		return TOPIC_PREFIX_DAT + deviceId;
	}

	// return topic:TC/CMD/{devId}
	private static String encodeTopicCmd(String deviceId) {
		return TOPIC_PREFIX_CMD + deviceId;
	}
	
	// return deviceId
	private static String decodeTopicCmd(String topic) {
		return topic.substring(TOPIC_PREFIX_CMD.length());
	}
	
}
