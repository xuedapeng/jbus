package cc.touchuan.jbus.session;

import io.netty.channel.Channel;

public class Session {

	private String sessionId;
	private Channel channel;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	
	
	
	
}
