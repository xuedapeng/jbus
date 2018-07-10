package cc.touchuan.jbus.mqtt;

import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;

import cc.touchuan.jbus.common.conf.ZSystemConfig;
import cc.touchuan.jbus.common.exception.JbusException;
import io.netty.util.internal.StringUtil;

public class MqttPoolManager {

	static String BROKER = ZSystemConfig.getProperty("mqtt.broker");
	static String USERNAME = ZSystemConfig.getProperty("mqtt.auth.account");
	static String PASSWORD = ZSystemConfig.getProperty("mqtt.auth.password");
	 
	static String BROKER_LOCAL = ZSystemConfig.getProperty("mqtt.local.broker");
	static String USERNAME_LOCAL = ZSystemConfig.getProperty("mqtt.local.auth.account");
	static String PASSWORD_LOCAL = ZSystemConfig.getProperty("mqtt.local.auth.password");
	 
	static MqttPool _mqttPool = new MqttPool();
	static MqttPool _mqttPool_local = null;
	
	public static void initialize() {
		if (StringUtil.isNullOrEmpty(BROKER)) {
			
			throw new JbusException("need mqtt broker server.");
		}
		
		_mqttPool.initialize(BROKER, USERNAME, PASSWORD);
		
		if (!StringUtil.isNullOrEmpty(BROKER_LOCAL)) {
			_mqttPool_local = new MqttPool();
			_mqttPool_local.initialize(BROKER_LOCAL, USERNAME_LOCAL, PASSWORD_LOCAL);
		}
	}
	
	public static MqttPool getMqttPool() {
		return _mqttPool;
	}

	public static MqttPool getMqttPoolLocal() {
		return _mqttPool_local;
	}


	/*
	 * 提供给json-Rpc的接口 
	 */
	
	public static class JsonRpc {
		
		public static String getMqttIdByDeviceId(String deviceId) {
			return _mqttPool._deviceId2MqttClientMap.get(deviceId).getClientId();
		}
		
		public static List<MqttClient> getMqttClientList() {
			
			return _mqttPool._mqttClientList;
		}
		
		public static List<String> getDeviceIdsOfMqttClient(String mqttId) {
			return _mqttPool._clientId2DeviceIdMap.get(mqttId);
		}
		
		
		public static String getMqttIdByDeviceIdLocal(String deviceId) {
			
			if (_mqttPool_local == null) {
				return null;
			}
			
			return _mqttPool_local._deviceId2MqttClientMap.get(deviceId).getClientId();
		}
		
		public static List<MqttClient> getMqttClientListLocal() {

			if (_mqttPool_local == null) {
				return null;
			}
			
			return _mqttPool_local._mqttClientList;
		}
		
		public static List<String> getDeviceIdsOfMqttClientLocal(String mqttId) {

			if (_mqttPool_local == null) {
				return null;
			}
			
			return _mqttPool_local._clientId2DeviceIdMap.get(mqttId);
		}
		
	}

	
}
