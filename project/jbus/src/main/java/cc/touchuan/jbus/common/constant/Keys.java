package cc.touchuan.jbus.common.constant;

import io.netty.util.AttributeKey;

public class Keys {


	public static final AttributeKey<String> SESSION_ID_KEY = AttributeKey.valueOf("SESSION_ID");
	
	// 1:tc;2:websocket
	public static final AttributeKey<String> CHANNEL_TYPE_KEY = AttributeKey.valueOf("CHANNEL_TYPE");
	
	
	public static final AttributeKey<String> HEART_INTERVAL_KEY = AttributeKey.valueOf("HEART_INTERVAL");   
	
	
}
