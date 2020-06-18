package cc.touchuan.jbus.application;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.conf.ZSystemConfig;
import cc.touchuan.jbus.common.exception.JbusException;
import cc.touchuan.jbus.mqtt.MqttPoolManager;
import cc.touchuan.jbus.plugin.loadtestor.LoadTestor;
import cc.touchuan.jbus.session.SessionManager;

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
		int portWs = Integer.valueOf(ZSystemConfig.getProperty("listener.ws.port"));
		
		LOG.info("config_path:" + ZSystemConfig.getSystemConfigPath());
		
		// 全局配置
		Global.config();

		// 初始化mqtt pool
		MqttPoolManager.initialize();
		// 初始化SessionManager
		SessionManager.initialize();
		
		// 透传服务器
		new Thread() {

			@Override
			public void run() {
				try {
					new TcServer(portTcp).start();
				} catch (Exception e) {

					LOG.error(JbusException.trace(e), e);
				}
			}
			
		}.start();

		// 透传服务器(websocket)
		new Thread() {

			@Override
			public void run() {
				try {
					new WsServer(portWs).start();
				} catch (Exception e) {

					LOG.error(JbusException.trace(e), e);
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

					LOG.error(JbusException.trace(e), e);
				}
			}
			
		}.start();
			
    }
}
