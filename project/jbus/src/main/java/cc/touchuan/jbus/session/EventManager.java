package cc.touchuan.jbus.session;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import cc.touchuan.jbus.common.helper.JsonBuilder;
import cc.touchuan.jbus.common.helper.JsonHelper;
import cc.touchuan.jbus.proxy.MqttProxy;

public class EventManager {

	public static final String EVENT_ON = "on";
	public static final String EVENT_OFF = "off";
	
	
	public static void publish(Event event) {

		/* 主题：TC/STS/{deviceSn}
           内容：{"deviceSn":"4755B9F2","tcpClient":"60.167.19.237:4097","time":"2018-08-15 17:05:04","event":"on/off","sessionId":"","onlineCount":"1"}
		*/
		
		String deviceSn = event.getSession().getDeviceProxy().getDeviceId();
		String tcpClient = event.getSession().getHost() + ":" + event.getSession().getPort();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eventName = event.getEventName();
		String sessionId = event.getSession().getSessionId();
		int onlineCount = Optional.ofNullable(SessionManager._device2SessionMap.get(deviceSn)).orElse(Collections.emptyList()).size();
		
		String topic = MqttProxy.TOPIC_PREFIX_STS + deviceSn;
		
		String data = JsonBuilder.build()
				.add("deviceSn",deviceSn)
				.add("tcpClient",tcpClient)
				.add("time",time)
				.add("event", eventName)
				.add("sessionId", sessionId)
				.add("onlineCount", String.valueOf(onlineCount))
				.toString();
		
		MqttProxy.publishEvent(topic, data);
		
		
	}
}
