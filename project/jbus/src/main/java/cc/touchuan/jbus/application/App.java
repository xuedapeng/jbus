package cc.touchuan.jbus.application;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.conf.ZSystemConfig;
import cc.touchuan.jbus.plugin.loadtestor.LoadTestor;

/**
 * Hello world!
 *
 */
public class App {

	static Logger LOG = Logger.getLogger(App.class);
	
    public static void main( String[] args ) throws Exception 
    {
    	
		int portTcp = Integer.valueOf(ZSystemConfig.getProperty("listener.tcp.port"));
		int portRpc = Integer.valueOf(ZSystemConfig.getProperty("listener.rpc.port"));
		
		LOG.info("config_path:" + ZSystemConfig.getSystemConfigPath());
		
		// 全局配置
		Global.config();
			
		// 透传服务器
		new Thread() {

			@Override
			public void run() {
				try {
					new TcServer(portTcp).start();
				} catch (Exception e) {

					LOG.error("", e);
				}
			}
			
		}.start();
		

		// RPC服务器
		new Thread() {

			@Override
			public void run() {
				try {
					new JrServer(portRpc).start();
				} catch (Exception e) {

					LOG.error("", e);
				}
			}
			
		}.start();
			
    }
}
