package cc.touchuan.jbus.plugin.jsonrpc.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.touchuan.jbus.common.helper.JsonBuilder;
import cc.touchuan.jbus.mqtt.MqttPool;
import cc.touchuan.jbus.session.Session;
import cc.touchuan.jbus.session.SessionManager;

public class GetDeviceInfoLogic extends RpcLogic {

	@Override
	public String execute(Map<String, Object> data) {
		
		List<String> list = SessionManager.JsonRpc.getDeviceList();
		
		int totalCount = list.size();
		
		if(totalCount > 1000) {
			list = list.subList(0, 1000);
		}
		
		List<Object> resultList = new ArrayList<Object>();
		
		list.forEach((deviceId)->{
			Map<String, Object> map = new HashMap<String, Object>();
			
			List<Session> sessionList = SessionManager.findByDeviceId(deviceId);
			List<String> sessionIds = new ArrayList<String>();
			sessionList.forEach((session)->{
				sessionIds.add(session.getSessionId());
			});
			
			map.put("deviceId", deviceId);
			map.put("sessionIds", sessionIds);
			resultList.add(map);
		});
		
		
		String res = JsonBuilder.build()
				.add("status", "0")
				.add("msg", "getDeviceInfo ok.")
				.add("totalCount", totalCount)
				.add("resultCount", list.size())
				.add("result", resultList)
				.toString();
				
				
		return res;
	}

}
