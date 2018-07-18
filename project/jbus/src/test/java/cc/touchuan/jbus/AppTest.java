package cc.touchuan.jbus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.paho.client.mqttv3.logging.Logger;

import cc.touchuan.jbus.common.helper.ByteHelper;
import cc.touchuan.jbus.plugin.loadtestor.DeviceSimulator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    
    private void initDeviceSimulator(List<DeviceSimulator> list) {

		new Thread() {
			@Override
			public void run() {
	    		try {
	    			DeviceSimulator ds = new DeviceSimulator("jbus.bizmsg.cn", 2883);

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
    
    public void testNone() {
    	
    }
    /**
     * Rigourous Test :-)
     * @throws Exception 
     */
    public void ntestApp() throws Exception
    {
    	final List<DeviceSimulator> list = new ArrayList<DeviceSimulator>();

		new Thread() {
			@Override
			public void run() {
		    	for (int i=0; i<400; i++) {
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

    		Thread.sleep(5000);
        	System.out.println(list.size());
        	
        	int count = list.size();
    		for(int i=0; i< count; i++) {
    			DeviceSimulator ds = list.get(i);
        		Thread.sleep(100);
    			if (ds == null || ds.deviceId == null) {
    				continue;
    			}
    			String msg = String.format("msg from %s [%s]", ds.deviceId, new Date().toString());
    			ds.channel.writeAndFlush(ByteHelper.str2bb(msg));
    			
    			System.out.println("DS:" + msg);
    		}

    	}
    	
    }
		
		
}
