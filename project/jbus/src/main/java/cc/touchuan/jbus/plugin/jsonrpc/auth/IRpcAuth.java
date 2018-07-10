package cc.touchuan.jbus.plugin.jsonrpc.auth;

public interface IRpcAuth {
	
	public boolean checkByToken(String appId, String appToken);

}
