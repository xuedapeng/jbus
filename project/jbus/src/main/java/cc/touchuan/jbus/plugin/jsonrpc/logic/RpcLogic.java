package cc.touchuan.jbus.plugin.jsonrpc.logic;

import java.util.Map;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.helper.JsonHelper;
import cc.touchuan.jbus.plugin.jsonrpc.auth.RpcAuthProxy;

public abstract class RpcLogic {
	
	static Logger logger = Logger.getLogger(RpcLogic.class);
	
	public abstract String execute(Map<String, Object> data);
	

	@SuppressWarnings("unchecked")
	protected  boolean auth(String param) {

		Map<String, Object> paramMap = JsonHelper.json2map(param);
		
		String appId = ((Map<String, String>)paramMap.get("auth")).get("appId");
		String appToken = ((Map<String, String>)paramMap.get("auth")).get("appToken");
		
		return RpcAuthProxy.checkByToken(appId, appToken);
	}
	
	@SuppressWarnings("unchecked")
	protected boolean validate(String param) {

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
