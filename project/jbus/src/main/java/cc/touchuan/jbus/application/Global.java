package cc.touchuan.jbus.application;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import cc.touchuan.jbus.common.conf.ZSystemConfig;

public class Global {
	
	public static void config() {

		// log4j
		String log4jConfig = ZSystemConfig.getProperty("log4j.config");
		if (log4jConfig != null) {
			if (log4jConfig.indexOf("/") < 0) {
				log4jConfig =  new File(ZSystemConfig.getSystemConfigPath(),log4jConfig).getAbsolutePath();
			}
			

			PropertyConfigurator.configure(log4jConfig);  
		}
	}
	
	// 心跳设置
	public static final Integer HEART_BEAT_DEFAULT = 1; // 10秒
	public static final Integer HEART_BEAT_TEN_MINUTES = 60; // 600秒
	public static final Integer HEART_BEAT_MAX = 8640; // 24小时
	public static final Integer HEART_BEAT_NONE = 8641; // 24小时+
	
	// 服务类型

	public static final String CHANNEL_TYPE_TC = "tc"; 
	public static final String CHANNEL_TYPE_WS = "ws"; 
	public static final String CHANNEL_TYPE_HTTP = "http"; 
	
	

}
