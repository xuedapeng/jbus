package cc.touchuan.jbus.plugin.jsonrpc.logic;

import java.util.HashMap;
import java.util.Map;

import cc.touchuan.jbus.common.conf.ZSystemConfig;
import cc.touchuan.jbus.common.helper.JsonBuilder;
import cc.touchuan.jbus.mqtt.MqttPoolManager;
import cc.touchuan.jbus.session.SessionManager;

public class GetDashboardLogic extends RpcLogic {

	@Override
	public String execute(Map<String, Object> data) {
	
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("tcp_port", ZSystemConfig.getProperty("listener.tcp.port"));
		resultMap.put("rpc_port", ZSystemConfig.getProperty("listener.rpc.port"));
		resultMap.put("mqtt_broker", ZSystemConfig.getProperty("mqtt.broker"));
		resultMap.put("mqtt_local_broker", ZSystemConfig.getProperty("mqtt.local.broker"));
		
		resultMap.put("sessionCount", SessionManager.JsonRpc.getSessionList().size());
		resultMap.put("deviceCount", SessionManager.JsonRpc.getDeviceList().size());
		resultMap.put("mqttClientCount", MqttPoolManager.JsonRpc.getMqttClientList().size());
		
		if (MqttPoolManager.JsonRpc.getMqttClientListLocal() != null) {
			resultMap.put("mqttClientCountLocal", MqttPoolManager.JsonRpc.getMqttClientListLocal().size());
		}
		
		
		String res = JsonBuilder.build()
				.add("status", "0")
				.add("msg", "getDashboard ok.")
				.add("result", resultMap)
				.toString();
				
				
		return res;
	}

}
