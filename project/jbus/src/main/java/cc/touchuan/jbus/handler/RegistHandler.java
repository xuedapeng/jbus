package cc.touchuan.jbus.handler;


import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.auth.DeviceAuth;
import cc.touchuan.jbus.common.constant.Keys;
import cc.touchuan.jbus.common.exception.JbusException;
import cc.touchuan.jbus.common.helper.ByteHelper;
import cc.touchuan.jbus.proxy.DeviceProxy;
import cc.touchuan.jbus.proxy.DeviceProxyManager;
import cc.touchuan.jbus.session.Session;
import cc.touchuan.jbus.session.SessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.CharsetUtil;

@Sharable 
public class RegistHandler extends ChannelInboundHandlerAdapter {

	static Logger logger = Logger.getLogger(RegistHandler.class);
	
	final static String REG_START = "REG:";
	final static String REG_SPLIT = ",";
	final static String REG_END = ";";
	
	static enum REG_STATUS  {OK, IGNORE, ERROR, FAIL};

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		logger.info("channelActive");
		
		// 获取客户端地址
		Object[] fromAddr = this.getFromAddr(ctx);
		String host = (String)fromAddr[0];
		int port = (Integer)fromAddr[1];
		
		
		// 注册订阅
		regByAddress(ctx, host, port);
		
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		// 以字符 REG 开头的是注册消息
		ByteBuf in = (ByteBuf) msg; 
		ByteBuf inReg = in.slice();
		
		REG_STATUS status = regByToken(ctx, inReg);
		
		// 不是注册消息:下一棒
		if (status.equals(REG_STATUS.IGNORE)) {
			ctx.fireChannelRead(msg);
			return;
		}
		
		// 注册成功：删除本handler
		if (status.equals(REG_STATUS.OK)) {
			ctx.pipeline().remove(RegistHandler.class);
			logger.info("remove RegistHandler");
		} else {

			logger.info("status:" + status);
		}
 	} 

	private REG_STATUS regByAddress(ChannelHandlerContext ctx, String host, int port) {

		// 鉴权
		if (!DeviceAuth.checkFromAddr(host, port)) {
			regError(ctx, host + ":" + String.valueOf(port));
			return REG_STATUS.FAIL;
		}

		// session,proxy管理
		Session session = SessionManager.createSession(ctx.channel(), host, port);
		DeviceProxyManager.createProxy(session.getSessionId(), host, port);
		
		return REG_STATUS.OK;
	}
	
	
	private REG_STATUS regByToken(ChannelHandlerContext ctx, ByteBuf inReg) {

		// 长度
		if (inReg.readableBytes() < REG_START.length() ) {
			return REG_STATUS.IGNORE;
		}
		
		// 开始标记
		byte[] regFlg = new byte[REG_START.length()];
		inReg.readBytes(regFlg);
		if (!REG_START.equals(new String(regFlg, CharsetUtil.UTF_8))) {
			return REG_STATUS.IGNORE;
		}
		
		// 分隔符、结尾符
		String regInfo = inReg.toString(CharsetUtil.UTF_8);
		int splitIdx = regInfo.indexOf(REG_SPLIT);
		if ((splitIdx < 1) 
				|| !regInfo.endsWith(REG_END)) {
			
			regError(ctx, regInfo);
			return REG_STATUS.ERROR;
		}
		
		String deviceId = regInfo.substring(0, splitIdx);
		String accessToken = regInfo.substring(splitIdx+1, regInfo.length()-1);
		
		// 鉴权
		if (!DeviceAuth.checkToken(deviceId, accessToken)) {
			regError(ctx, regInfo);
			return REG_STATUS.FAIL;
		}
		
		// 注册
		String sessionId = ctx.channel().attr(Keys.SESSION_ID_KEY).get();
		if (sessionId == null) {
			throw new JbusException("fail to find session of channel.");
		}
		Session session = SessionManager.findBySessionId(sessionId);
		if (session == null) {
			throw new JbusException("fail to find session by sessionId");
		}
		
		DeviceProxy proxy = DeviceProxyManager.findProxy(sessionId);
		if (proxy == null) {
			throw new JbusException("fail to find proxy by sessionId");
		}
		
		proxy.updateDeviceId(deviceId);
		
		return REG_STATUS.OK;
	}
	
	private void regError(ChannelHandlerContext ctx, String regInfo) {
		
		String outMsg = String.format("Auth failed, connection refused. data=[%s]", regInfo);
		ctx.channel().writeAndFlush(ByteHelper.str2bb(outMsg));
		ctx.close();
		
		// 释放资源
		SessionManager.closeSession(ctx.channel().attr(Keys.SESSION_ID_KEY).get());
	}
	
	// host,port
	private Object[] getFromAddr(ChannelHandlerContext ctx) {
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String host = insocket.getHostName();
		int port = insocket.getPort();
		
		return new Object[]{host, port};
	}
	
}
