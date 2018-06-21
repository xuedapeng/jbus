package cc.touchuan.jbus.proxy;

public class ControllerProxy {

	private String deviceId;
	private String sessionId;


	public ControllerProxy(String sessionId, String deviceId) {
		this.sessionId = sessionId;
		this.deviceId = deviceId;
		
		subscribe(deviceId);
		
	}

	public void updateDeviceId(String deviceId) {

		unSubscribe(this.deviceId);
		
		this.deviceId = deviceId;
		
		subscribe(this.deviceId);

		
	}
	
	public void close() {
		unSubscribe(this.deviceId);
	}
	
	
	private static  void subscribe(String deviceId) {
		
	}
	
	private static  void unSubscribe(String deviceId) {
		
	}
	
	
	
	
}
