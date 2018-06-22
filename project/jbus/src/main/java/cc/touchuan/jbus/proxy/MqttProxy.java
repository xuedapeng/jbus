package cc.touchuan.jbus.proxy;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import cc.touchuan.jbus.common.exception.JbusException;
import cc.touchuan.jbus.mqtt.MqttPool;

public class MqttProxy {

	static Logger LOG = Logger.getLogger(MqttPool.class);
	
	static String TOPIC_PREFIX = "TC/dev/";
	
	public static void subscribe(String deviceId) {
		
		try {
			MqttPool.getInstance(deviceId).subscribe(encodeTopic(deviceId));
		} catch (MqttException e) {
			LOG.error("订阅失败", e);
			throw new JbusException(e);
		}
	}
	
	public static void unSubscribe(String deviceId) {
		
		try {
			MqttPool.getInstance(deviceId).unsubscribe(encodeTopic(deviceId));
		} catch (MqttException e) {
			LOG.error("取消订阅失败", e);
			throw new JbusException(e);
		}
	}
	
	// controller 收到device发来的数据时，调用此方法
	public static void publish(String deviceId, byte[] data) {
		try {
			MqttPool.getInstance(deviceId).publish(
					encodeTopic(deviceId), 
					new MqttMessage(data));
			
		} catch (MqttException e) {
			LOG.error("发布失败", e);
			throw new JbusException(e);
		}
	}
	
	// mqtt client 收到推送时调用此方法
	public static void recieve(String topic, MqttMessage message) {
		String deviceId = decodeTopic(topic);
		byte[] command = message.getPayload();
		
		List<ControllerProxy> cpList = ControllerProxyManager.findByDeviceId(deviceId);
		
		cpList.forEach((P)->{
			P.sendCommand(command);
		});
	}
	
	// return topic:TC/dev/{devId}
	private static String encodeTopic(String deviceId) {
		return TOPIC_PREFIX + deviceId;
	}
	
	// return deviceId
	private static String decodeTopic(String topic) {
		return topic.substring(TOPIC_PREFIX.length());
	}
	
}
