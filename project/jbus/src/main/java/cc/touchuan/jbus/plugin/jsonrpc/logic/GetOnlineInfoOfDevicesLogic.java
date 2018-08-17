package cc.touchuan.jbus.plugin.jsonrpc.logic;

import java.util.List;
import java.util.Map;

import cc.touchuan.jbus.common.helper.JsonBuilder;
import cc.touchuan.jbus.common.helper.JsonHelper;
import cc.touchuan.jbus.session.SessionManager;

public class GetOnlineInfoOfDevicesLogic extends RpcLogic {

	@Override
	public String execute(Map<String, Object> data) {
		
		
		@SuppressWarnings("unchecked")
		Map<String, String> map = SessionManager.JsonRpc.getOnlineStateOfDevices((List<String>)data.get("deviceIds"));
		
		String res = JsonBuilder.build()
				.add("status", "0")
				.add("msg", "getOnlineInfoOfDevices ok.")
				.add("result", map)
				.toString();

		return res;
	}

	@SuppressWarnings("unchecked")
	protected boolean validate(String param) {

		if (!super.validate(param)) {
			return false;
		}
		
		Map<String, Object> paramMap = null;
		
		try {
			paramMap = JsonHelper.json2map(param);
		} catch(Exception e) {
			logger.error("", e);
			return false;
		}

		Map<String, Object> dataMap = (Map<String, Object>) paramMap.get("data");
		
		if (dataMap.containsKey("deviceIds")) {
			
			Object deviceIds = dataMap.get("deviceIds");
			if (deviceIds != null && deviceIds instanceof List) {
				if (((List<String>)deviceIds).size() > 0 ) {
					
					return true;
				}
			}
			
		}
		
		return false;
	}
	

}
