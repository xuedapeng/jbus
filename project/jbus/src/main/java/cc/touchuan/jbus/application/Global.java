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
	

}
