package cc.touchuan.jbus.plugin.jsonrpc.logic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.touchuan.jbus.common.helper.JsonBuilder;
import cc.touchuan.jbus.mqtt.MqttPool;
import cc.touchuan.jbus.mqtt.MqttPoolManager;
import cc.touchuan.jbus.session.Session;
import cc.touchuan.jbus.session.SessionManager;

public class GetSessionInfoLogic extends RpcLogic {

	@Override
	public String execute(Map<String, Object> data) {
		
		List<Session> list = SessionManager.JsonRpc.getSessionList();
		
		int totalCount = list.size();
		
		if(totalCount > 1000) {
			list = list.subList(0, 1000);
		}
		
		List<Object> resultList = new ArrayList<Object>();
		
		list.forEach((E)->{
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("sessionId", E.getSessionId());
			map.put("startTime", 
					new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(E.getStartTime()));
			map.put("host", E.getHost());
			map.put("port", E.getPort());
			map.put("isActive", E.getChannel().isActive());
			map.put("deviceId", E.getDeviceProxy().getDeviceId());
			map.put("mqttId", MqttPoolManager.JsonRpc.getMqttIdByDeviceId(E.getDeviceProxy().getDeviceId()));
			
			String mqttIdLocal = MqttPoolManager.JsonRpc.getMqttIdByDeviceIdLocal(E.getDeviceProxy().getDeviceId());
			map.put("mqttIdLocal", mqttIdLocal==null?"":mqttIdLocal);
			resultList.add(map);
		});
		
		String res = JsonBuilder.build()
				.add("status", "0")
				.add("msg", "getSessionInfo ok.")
				.add("totalCount", totalCount)
				.add("resultCount", list.size())
				.add("result", resultList)
				.toString();

		return res;
	}

}
