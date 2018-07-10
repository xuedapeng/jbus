package cc.touchuan.jbus.plugin.jsonrpc.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttClient;

import cc.touchuan.jbus.common.helper.JsonBuilder;
import cc.touchuan.jbus.mqtt.MqttPoolManager;

public class GetMqttInfoLogic extends RpcLogic {

	@Override
	public String execute(Map<String, Object> data) {
		
		List<MqttClient> list = MqttPoolManager.JsonRpc.getMqttClientList();
		List<MqttClient> listLocal = MqttPoolManager.JsonRpc.getMqttClientListLocal();
		
		List<Object> resultList = new ArrayList<Object>();
		List<Object> resultListLocal = new ArrayList<Object>();
		
		list.forEach((client)->{
			Map<String, Object> map = new HashMap<String, Object>();
			
			
			map.put("mqttId", client.getClientId());
			map.put("server", client.getServerURI());
			map.put("isConnected", client.isConnected());
			map.put("deviceIds", MqttPoolManager.JsonRpc.getDeviceIdsOfMqttClient(client.getClientId()));
			resultList.add(map);
		});
		
		if (listLocal != null) {
			listLocal.forEach((client)->{
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put("mqttId", client.getClientId());
				map.put("server", client.getServerURI());
				map.put("isConnected", client.isConnected());
				map.put("deviceIds", MqttPoolManager.JsonRpc.getDeviceIdsOfMqttClientLocal(client.getClientId()));
				resultListLocal.add(map);
			});
		}
		
		
		String res = JsonBuilder.build()
				.add("status", "0")
				.add("msg", "getMqttInfo ok.")
				.add("result", resultList)
				.add("resultLocal", resultListLocal)
				.toString();
				
				
		return res;
	}

}
