package cc.touchuan.jbus.application;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.conf.ZSystemConfig;

/**
 * Hello world!
 *
 */
public class App {

	static Logger LOG = Logger.getLogger(App.class);
	
    public static void main( String[] args )
    {
		int port = Integer.valueOf(ZSystemConfig.getProperty("listener.tcp.port"));
		
		LOG.info("config_path:" + ZSystemConfig.getSystemConfigPath());
		
		
		try {
			
			new TcServer( port).start(); // 调用服务器的start()方法 
			
		} catch (Exception e) {
			
			LOG.error("", e);
			
		} 
    }
}
