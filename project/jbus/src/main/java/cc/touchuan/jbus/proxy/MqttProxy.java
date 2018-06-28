package cc.touchuan.jbus.proxy;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import cc.touchuan.jbus.common.exception.JbusException;
import cc.touchuan.jbus.common.helper.HexHelper;
import cc.touchuan.jbus.mqtt.MqttPool;

public class MqttProxy {

	static Logger LOG = Logger.getLogger(MqttProxy.class);
	
	static String TOPIC_PREFIX_CMD = "TC/CMD/";
	static String TOPIC_PREFIX_DAT = "TC/DAT/";
	
	public static void subscribe(String deviceId) {

		LOG.info(String.format("subscribe: %s", 
				encodeTopicCmd(deviceId)));
		
		try {
			MqttPool.getInstance(deviceId).subscribe(encodeTopicCmd(deviceId));
		} catch (MqttException e) {
			LOG.error("订阅失败", e);
			throw new JbusException(e);
		}
	}
	
	public static void unSubscribe(String deviceId) {

		LOG.info(String.format("unSubscribe: %s", 
				encodeTopicCmd(deviceId)));
		
		try {
			MqttPool.getInstance(deviceId).unsubscribe(encodeTopicCmd(deviceId));
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
			MqttPool.getInstance(deviceId).publish(
					encodeTopicDat(deviceId), 
					new MqttMessage(data));
			
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
