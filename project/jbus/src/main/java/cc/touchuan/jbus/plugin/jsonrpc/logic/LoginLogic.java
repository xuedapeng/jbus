package cc.touchuan.jbus.plugin.jsonrpc.logic;

import java.util.Map;

import cc.touchuan.jbus.common.conf.ZSystemConfig;
import cc.touchuan.jbus.common.helper.JsonBuilder;
import cc.touchuan.jbus.common.helper.JsonHelper;

public class LoginLogic extends RpcLogic {

	static String _expectedAccount = ZSystemConfig.getProperty("admin.account");
	static String _expectedPassword = ZSystemConfig.getProperty("admin.password");
	public static String _adminAppId = ZSystemConfig.getProperty("admin.jsonrpc.appId");
	public static String _adminAppToken = ZSystemConfig.getProperty("admin.jsonrpc.appToken");
	
	@Override
	public String execute(Map<String, Object> data) {
		
		String res = null;
		
		String account = (String) data.get("account");
		String password = (String) data.get("password");
		
		
		if (!_expectedAccount.equals(account)
				|| !_expectedPassword.equals(password)) {
			
			res = JsonBuilder.build()
					.add("status", "-10")
					.add("msg", "invalid account/password")
					.toString();
			
			return res;
			
		}

		res = JsonBuilder.build()
				.add("status", "0")
				.add("msg", "login ok")
				.add("appId", _adminAppId)
				.add("appToken", _adminAppToken)
				.toString();
		
				
		return res;
	}
	
	@Override
	protected boolean auth(String param) {
		return true;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	protected boolean validate(String param) {

		Map<String, Object> paramMap = null;
		
		try {
			paramMap = JsonHelper.json2map(param);
		} catch(Exception e) {
			logger.error("", e);
			return false;
		}
		
		// 登录
		if (paramMap.containsKey("data")) {
			
			Map<String, String> dataMap = (Map<String, String>)paramMap.get("data");

			if (dataMap.containsKey("account")
					&& dataMap.containsKey("password")) {
				
				return true;
			}
		}
		
		return false;
	}

}
