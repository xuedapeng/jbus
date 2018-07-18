package cc.touchuan.jbus.plugin.loadtestor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttException;

import cc.touchuan.jbus.common.helper.ByteHelper;

public class LoadTestor {

	static String tcp_ip;
	static int tcp_port;
	static int connection_count;
	
	static String mqtt_server;
	static String mqtt_user;
	static String mqtt_password;
	static int device_count;

	static int startId_dev;
	static int startId_ctrl;
	
	public static void main(String startId, String devcount) throws Exception {

		String[] arr = {"loadtest", "device", "jbus.bizmsg.cn","2883",startId,devcount };
		String[] arr2 = {"loadtest", "ctrl", "tcp://pms.bizmsg.net:1883","jbus","jbus",startId, devcount };
		
		new Thread() {
			@Override
			public void run() {

				try {
					LoadTestor.run(arr);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
		

		new Thread() {
			@Override
			public void run() {

				try {
					LoadTestor.run(arr2);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
		

		
	}
	
	public static void run(String[] args) throws Exception {
		
		if (args[1].equals("device")) {
			tcp_ip = args[2];
			tcp_port = Integer.valueOf(args[3]);
			startId_dev =  Integer.valueOf(args[4]);
			connection_count = Integer.valueOf(args[5]);
			new LoadTestor().testDevice();
		}
		
		if (args[1].equals("ctrl")) {
			mqtt_server = args[2];
			mqtt_user = args[3];
			mqtt_password = args[4];
			startId_ctrl=  Integer.valueOf(args[5]);
			device_count = Integer.valueOf(args[6]);
			new LoadTestor().testCtrl();
		}
		
		
	}
	
    public void testDevice() throws Exception
    {
    	final List<DeviceSimulator> list = new ArrayList<DeviceSimulator>();
    	DeviceSimulator.maxDeviceId = startId_dev;

		new Thread() {
			@Override
			public void run() {
		    	for (int i=0; i<connection_count; i++) {
		    		initDeviceSimulator(list);
		    		try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
			}
		}.start();
		
    	System.out.println("aaa");

//		Thread.sleep(20000);
    	while(true) {

    		Thread.sleep(1000);
        	System.out.println(list.size());
        	
        	int count = list.size();
    		for(int i=0; i< count; i++) {

        		Thread.sleep(100);
        		
//    			System.out.println("ds:" + i);
    			DeviceSimulator ds = list.get(i);
    			if (ds == null || ds.deviceId == null || ds.channel == null || !ds.channel.isActive()) {
    				continue;
    			}
    			String msg = String.format("msg from %s [%s]", ds.deviceId, new Date().toString());
    			ds.channel.writeAndFlush(ByteHelper.str2bb(msg));
    			
    			System.out.println("DS:" + msg);
    		}

    	}
    	
    }
    

    private void initDeviceSimulator(List<DeviceSimulator> list) {

		new Thread() {
			@Override
			public void run() {
	    		try {
	    			DeviceSimulator ds = new DeviceSimulator(tcp_ip, tcp_port);

	    			list.add(ds);

	        		System.out.println(ds.deviceId);
	    			ds.start();

					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
    }
    
    private void testCtrl() throws MqttException, InterruptedException {
    	List<CtrlSimulator> csList = new ArrayList<CtrlSimulator>();
    	

		System.out.println("testCtrl start:");
		
    	for (int i=0; i<10; i++) {
    		CtrlSimulator cs = new CtrlSimulator(mqtt_server, mqtt_user, mqtt_password);
    		csList.add(cs);
    		cs.start();
    		System.out.println("CtrlSimulator created:" + i);
    	}
    	
//    	Thread.sleep(10*1000);
    	
    	int perSize = device_count/csList.size();
    	
    	
    	int count = startId_ctrl;
    	for(CtrlSimulator cs: csList) {
    		
    		for(int i=0; i<=perSize; i++) {
        		count++;
        		String deviceId = "dev_" + count;
        		
        		while(cs == null || cs._client == null || !cs._client.isConnected()) {

                	Thread.sleep(100);
                	System.out.println("wait client create."+count);
        		}
        		cs._client.subscribe("TC/DAT/"+deviceId);
        		System.out.println("subscribe:"+ "TC/CMD/"+deviceId);
    		}
    	}
    	
    	int whileCount = 0;
    	while(true) {
    		whileCount++;
    		System.out.println("while in testCtrl:"+whileCount);
        	Thread.sleep(600*1000);
    	}
    	
    }
    
    
		
}
