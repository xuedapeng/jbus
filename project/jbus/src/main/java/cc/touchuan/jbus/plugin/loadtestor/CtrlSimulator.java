package cc.touchuan.jbus.plugin.loadtestor;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import cc.touchuan.jbus.common.exception.JbusException;
import cc.touchuan.jbus.common.helper.ByteHelper;
import cc.touchuan.jbus.common.helper.CryptoHelper;
import cc.touchuan.jbus.proxy.MqttProxy;

public class CtrlSimulator {

	String broker;
	String user;
	String password;
	MqttClient _client;
	
	public CtrlSimulator(String broker, String user, String password) {
		this.broker = broker;
		this.user = user;
		this.password = password;
	}
	
	public void start() {
		new Thread() {
			@Override
			public void run() {
				runMqtt();
			}
		}.start();
		
	}
	
	private void runMqtt() {

		 MqttConnectOptions _connOpts = new MqttConnectOptions(); 
		 
		_connOpts.setCleanSession(true);  
		_connOpts.setUserName(user);  
		_connOpts.setPassword(password.toCharArray());  
		_connOpts.setConnectionTimeout(10);  
		_connOpts.setKeepAliveInterval(20); 
        
		try {
			_client = createMqttClient(_connOpts);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private  MqttClient createMqttClient(MqttConnectOptions _connOpts) throws MqttException {
		
        MemoryPersistence persistence = new MemoryPersistence();  
        
        
        MqttClient mqttClient = new MqttClient(broker, CryptoHelper.genUUID(), persistence);  
        mqttClient.setCallback(new MqttCallback(){

			@Override
			public void connectionLost(Throwable cause) {
				cause.printStackTrace();
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
				
			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				
				System.out.println(String.format("recieve: %s, %s", topic, message.toString()));
				
				String cmdTopic = topic.replace("DAT", "CMD");
				
				String sendData = "RE:"+ message.toString();
				mqttClient.publish(cmdTopic, new MqttMessage(ByteHelper.str2bytes(sendData)));

				System.out.println(String.format("CS:send:%s,%s",cmdTopic, sendData));
			}
        	
        });  
        
        
        mqttClient.connect(_connOpts);  
        return mqttClient;  
	}

}
