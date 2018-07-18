package cc.touchuan.jbus.plugin.jsonrpc.auth;

import cc.touchuan.jbus.plugin.jsonrpc.logic.LoginLogic;

public class RpcAuthProxy {

	public static boolean checkByToken(String appId, String appToken) {
		
		if(LoginLogic._adminAppId.equals(appId) 
				&& LoginLogic._adminAppToken.equals(appToken)) {
			return true;
		}
		return false;
	}
}
