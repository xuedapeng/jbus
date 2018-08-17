package cc.touchuan.jbus.mqtt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import cc.touchuan.jbus.common.conf.ZSystemConfig;
import cc.touchuan.jbus.common.exception.JbusException;
import cc.touchuan.jbus.common.helper.CryptoHelper;
import cc.touchuan.jbus.proxy.MqttProxy;

public class MqttPool {

	static Logger logger = Logger.getLogger(MqttPool.class);
	
	// deviceId->MqttClient
	 Map<String, MqttClient> _deviceId2MqttClientMap = new ConcurrentHashMap<String, MqttClient>();

	// clientId->deviceCount
	 Map<String, Integer> _clientId2Count = new ConcurrentHashMap<String, Integer>();

	// clientId->deviceIdList
	 Map<String, List<String>> _clientId2DeviceIdMap = new ConcurrentHashMap<String, List<String>>();
	
	// clientId->MqttClient
	 Map<String, MqttClient> _clientId2MqttClient = new ConcurrentHashMap<String, MqttClient>();
	
	// MqttClient
	 List<MqttClient> _mqttClientList = new ArrayList<MqttClient>();
	
	// connection lost
	 List<MqttClient> _mqttClientDisconnectedList = new ArrayList<MqttClient>();
	
	 private String mqttBroker;
	 private String mqttUsername;
	 private String mqttPassword;
	
	 int INIT_SIZE = 10;
	 final long IDLE_SLEEP = 10*1000;// 休眠10秒；
	
	 MqttConnectOptions _connOpts = new MqttConnectOptions(); 
	
	// 初始创建10个连接
	public  void initialize(String broker, String username, String password) {
		mqttBroker = broker;
		mqttUsername = username;
		mqttPassword = password;
		
		initPool();
		checkLostTask();
	}
	
	private  void initPool() {

		_connOpts.setCleanSession(true);  
		_connOpts.setUserName(mqttUsername);  
		_connOpts.setPassword(mqttPassword.toCharArray());  
		_connOpts.setConnectionTimeout(10);  
		_connOpts.setKeepAliveInterval(20); 
        
		try {
			for (int i=0; i<INIT_SIZE; i++ ) {
					MqttClient client = createMqttClient();
					_mqttClientList.add(client);
					_clientId2MqttClient.put(client.getClientId(), client);
					_clientId2Count.put(client.getClientId(), 0);
					_clientId2DeviceIdMap.put(client.getClientId(), new ArrayList<String>());
					
					logger.info("MqttClient created. clientId=" + client.getClientId());
			}
			
		} catch (MqttException e) {
			
			logger.error("无法连接到mqtt服务器。", e);
			throw new JbusException(e);
		}
	}
	
	// 重连线程
	private  void checkLostTask() {
		
		new Thread() {

			@Override  
			public void run() {
				
				while(true) {
					
					if (_mqttClientDisconnectedList.size() > 0) {
						reconnect();
						logger.info("_mqttClientDisconnectedList.size=" + _mqttClientDisconnectedList.size());
					}

					
					try {
						sleep(IDLE_SLEEP);
					} catch (InterruptedException e) {
						logger.error("", e);
					}
				}
			}
			
		}.start();
	}
	
	private  synchronized void reconnect() {

		List<MqttClient> successList = new ArrayList<MqttClient>();
		
		// 重连接
		_mqttClientDisconnectedList.forEach((E)->{
			try {
				if (!E.isConnected()) {
					E.connect(_connOpts);
					logger.info("reconnected. client=" + E.getClientId());
				}
				successList.add(E);
			} catch (MqttException e) {
				logger.error("", e);
			}
		});
		
		// 从失连列表中删除重连成功的对象
		successList.forEach((E)->{
			_mqttClientDisconnectedList.remove(E);
		});
		
		// 全面检查
		_mqttClientList.forEach((E)->{

			if (!E.isConnected()) {
				if (!_mqttClientDisconnectedList.contains(E)) {
					_mqttClientDisconnectedList.add(E);
				}
			}
		});

		
	}

	public  MqttClient getInstance() {
		return whoNotBusy();
	}
	
	public  MqttClient getInstance(String deviceId) {
		
		// 已存在deviceId
		if (_deviceId2MqttClientMap.containsKey(deviceId)) {
			return _deviceId2MqttClientMap.get(deviceId);
		} 

		// 新deviceId
		MqttClient instance = whoNotBusy();
		_deviceId2MqttClientMap.put(deviceId, instance);
		
		// 计数
		counter(instance.getClientId(), 1);
		
		// clientId =>  DeviceIds
		_clientId2DeviceIdMap.get(instance.getClientId()).add(deviceId);
		
		return instance;
		
	}
	
	public  void release(String deviceId) {
		MqttClient mc = _deviceId2MqttClientMap.remove(deviceId);
		counter(mc.getClientId(), -1);
		
		// clientId =>  DeviceIds
		_clientId2DeviceIdMap.get(mc.getClientId()).remove(deviceId);
		
	}
	
	private  void counter(String clientId, int plus) {

		int count = _clientId2Count.get(clientId);
		count = count + plus;
		_clientId2Count.put(clientId, count);
	}
	
	private  MqttClient createMqttClient() throws MqttException {
		
        MemoryPersistence persistence = new MemoryPersistence();  
        
        
        MqttClient mqttClient = new MqttClient(mqttBroker, makeClientId(), persistence);  
        mqttClient.setCallback(new MqttCallback(){

			@Override
			public void connectionLost(Throwable cause) {
				
				logger.info("", cause);
				// 重连
				if (!_mqttClientDisconnectedList.contains(mqttClient)) {
					_mqttClientDisconnectedList.add(mqttClient);
				}
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
				
				
			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				
				MqttProxy.recieve(topic, message);
				
			}
        	
        });  
        
        
        mqttClient.connect(_connOpts);  
        return mqttClient;  
	}
	
	// 找出负载在平均水平以下的任意一个
	private  MqttClient whoNotBusy() {
		
		// 平均负载
		int avg = _deviceId2MqttClientMap.size()/_clientId2MqttClient.size()+1;
		
		// 默认取第一个(函数式scope内要求final，故使用数组)
		String[] clientId = {_mqttClientList.get(0).getClientId()};
		
		// [java 8 函数式编程] 寻找负载较少的哪个
		_clientId2Count.forEach((K, V) -> {
			
			if (V <= avg) {
				clientId[0] = K;
				return;
			}
			
		});
		
		return _clientId2MqttClient.get(clientId[0]);
		
	}
	
	private  String makeClientId() {
		return CryptoHelper.genUUID();
	}

}
