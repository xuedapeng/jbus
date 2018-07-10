package cc.touchuan.jbus.plugin.jsonrpc.logic;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.helper.JsonBuilder;
import cc.touchuan.jbus.common.helper.JsonHelper;
import cc.touchuan.jbus.plugin.jsonrpc.auth.RpcAuthProxy;

public class LogicRouter {

	static Logger logger = Logger.getLogger(LogicRouter.class);
	
	static Map<String, Class<? extends RpcLogic>> _methodMap = new HashMap<String,Class<? extends RpcLogic>>();
	
	static {
		_methodMap.put("getSessionInfo", GetSessionInfoLogic.class);
		_methodMap.put("getDeviceInfo", GetDeviceInfoLogic.class);
		_methodMap.put("getMqttInfo", GetMqttInfoLogic.class);
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static String process(String param) {
		

		logger.info(param);
		
		if (!validate(param)) {

			String res = JsonBuilder.build()
					.add("status", "-3")
					.add("msg", "参数错误")
					.toString();
			
			logger.info(res);
			
			return res;
			
		}
		
		Map<String, Object> paramMap = JsonHelper.json2map(param);
		
		String appId = ((Map<String, String>)paramMap.get("auth")).get("appId");
		String appToken = ((Map<String, String>)paramMap.get("auth")).get("appToken");
		
		if (!auth(appId, appToken)) {
			String res = JsonBuilder.build()
					.add("status", "-2")
					.add("msg", "没有权限")
					.toString();

			logger.info(res);
			
			return res;
		}
		
		
		String method = (String) paramMap.get("method");
		if (!_methodMap.keySet().contains(method)) {
			String res = JsonBuilder.build()
					.add("status", "-3")
					.add("msg", "method 错误")
					.toString();

			logger.info(res);
			
			return res;
		}
		
		Map<String, Object> data = (Map<String, Object>)paramMap.get("data");
		
		try {
			RpcLogic logic = (RpcLogic)_methodMap.get(method).newInstance();
			String res = logic.execute(data);

			logger.info(res);
			
			return res;
			

		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("", e);
		}
		
		
		String res = JsonBuilder.build()
				.add("status", "-1")
				.add("msg", "未知错误")
				.toString();

		logger.info(res);
		
		return res;
	}
	
	private static boolean auth(String appId, String appToken) {
		return RpcAuthProxy.checkByToken(appId, appToken);
	}
	
	@SuppressWarnings("unchecked")
	private static boolean validate(String param) {

		Map<String, Object> paramMap = null;
		
		try {
			paramMap = JsonHelper.json2map(param);
		} catch(Exception e) {
			logger.error("", e);
			return false;
		}
		
		if (paramMap.containsKey("method") 
				&& paramMap.containsKey("data")
				&& paramMap.containsKey("auth")
				) {
			
			Map<String, String> authMap = (Map<String, String>)paramMap.get("auth");
			
			if (authMap.containsKey("appId")
					&& authMap.containsKey("appToken")) {
				
				return true;
			}
			
		}
		
		return false;
	}
	

}
