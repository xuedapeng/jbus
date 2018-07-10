package cc.touchuan.jbus.plugin.jsonrpc.logic;

import java.util.Map;

public abstract class RpcLogic {
	
	public abstract String execute(Map<String, Object> data);

}
