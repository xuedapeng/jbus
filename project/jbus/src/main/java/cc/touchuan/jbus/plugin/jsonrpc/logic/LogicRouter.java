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
		_methodMap.put("getOnlineInfoOfDevices", GetOnlineInfoOfDevicesLogic.class);
		_methodMap.put("getSessionInfo", GetSessionInfoLogic.class);
		_methodMap.put("getDeviceInfo", GetDeviceInfoLogic.class);
		_methodMap.put("getMqttInfo", GetMqttInfoLogic.class);
		_methodMap.put("getDashboard", GetDashboardLogic.class);
		_methodMap.put("login", LoginLogic.class);
		
	}
	
	@SuppressWarnings("unchecked")
	public static String process(String param) {
		
		String res = null;
		try {
			res = runProcess(param);
		} catch(Exception e) {

			res = JsonBuilder.build()
					.add("status", "-1")
					.add("msg", "未知错误")
					.toString();

			logger.info("",e);
		}
		
		
		return res;
	}
	
	private static RpcLogic getLogic(String param) {

		Map<String, Object> paramMap = null;
		
		try {
			paramMap = JsonHelper.json2map(param);
		} catch(Exception e) {
			logger.error("", e);
			return null;
		}
		
		if (!paramMap.containsKey("method")) {
			return null;
		}
		
		String method = (String)paramMap.get("method");
		
		if (!_methodMap.containsKey(method)) {
			return null;
		}
		
		try {
			
			return  _methodMap.get(method).newInstance();
			
		} catch (InstantiationException | IllegalAccessException e) {
			
			logger.error("", e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static String runProcess(String param) throws Exception {

		logger.info(param);
		
		RpcLogic logic = getLogic(param);
		
		// methed有效性
		if (logic == null) {
			String res = JsonBuilder.build()
					.add("status", "-3")
					.add("msg", "method 错误")
					.toString();

			logger.info(res);
			
			return res;
		}
		
		// 参数有效性
		if (!logic.validate(param)) {

			String res = JsonBuilder.build()
					.add("status", "-3")
					.add("msg", "参数错误")
					.toString();
			
			logger.info(res);
			
			return res;
			
		}
		
		// 鉴权有效性
		if (!logic.auth(param)) {
			String res = JsonBuilder.build()
					.add("status", "-2")
					.add("msg", "没有权限")
					.toString();

			logger.info(res);
			
			return res;
		}
		
		// 执行
		Map<String, Object> paramMap = JsonHelper.json2map(param);
		Map<String, Object> data = (Map<String, Object>)paramMap.get("data");
		
		String res = logic.execute(data);

		logger.info(res);
		
		return res;
			
	}

}
