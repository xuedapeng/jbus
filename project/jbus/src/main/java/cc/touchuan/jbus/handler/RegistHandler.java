package cc.touchuan.jbus.handler;


import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.application.Global;
import cc.touchuan.jbus.auth.DeviceAuthProxy;
import cc.touchuan.jbus.auth.db.DeviceTypeEntity;
import cc.touchuan.jbus.common.constant.Keys;
import cc.touchuan.jbus.common.exception.JbusException;
import cc.touchuan.jbus.common.helper.ByteHelper;
import cc.touchuan.jbus.common.helper.HexHelper;
import cc.touchuan.jbus.proxy.DeviceProxy;
import cc.touchuan.jbus.proxy.DeviceProxyManager;
import cc.touchuan.jbus.session.Event;
import cc.touchuan.jbus.session.EventManager;
import cc.touchuan.jbus.session.Session;
import cc.touchuan.jbus.session.SessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import cc.touchuan.jbus.common.helper.NumericHelper;

@Sharable 
public class RegistHandler extends ChannelInboundHandlerAdapter {

	static Logger logger = Logger.getLogger(RegistHandler.class);
	
	final static String REG_START = "REG";
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
		
		logger.info("receive:" + HexHelper.bytesToHexString(ByteHelper.bb2bytes((ByteBuf) msg)));
		
		// 以字符 REG 开头的是注册消息
		ByteBuf in = (ByteBuf) msg; 
		ByteBuf inReg = in.copy();
		
		REG_STATUS status = regByToken(ctx, inReg);
		ReferenceCountUtil.release(inReg);
		
		// 第三方协议设备
		if (status.equals(REG_STATUS.IGNORE)) {
			byte[] regBytes = ByteHelper.bb2bytes(in);
			
			byte[] regTokenBytes = regByFp(ctx, regBytes);
			
			if (regTokenBytes != null) {
				ByteBuf tokenReg = Unpooled.copiedBuffer(regTokenBytes);
				status = regByToken(ctx, tokenReg);
			}
		}
		
		// 不是注册消息:下一棒
		if (status.equals(REG_STATUS.IGNORE)) {
			ctx.fireChannelRead(msg);
			return;
		}
		
		// 注册成功：删除本handler
		if (status.equals(REG_STATUS.OK)) {
			ctx.pipeline().remove(RegistHandler.class);

			// 发给注册响应服务
			ctx.fireChannelRead(msg);
			logger.info("remove RegistHandler");
			return;
		} else {

			logger.info("status:" + status);
		}

		// tc 手动释放，ws自动释放
		if (Global.CHANNEL_TYPE_TC.equals(ctx.channel().attr(Keys.CHANNEL_TYPE_KEY).get())) {
			logger.info("release in(msg)");
			ReferenceCountUtil.release(msg);
		}
 	} 

	private REG_STATUS regByAddress(ChannelHandlerContext ctx, String host, int port) {

		// 鉴权
		if (!DeviceAuthProxy.checkFromAddr(host, port)) {
			regError(ctx, host + ":" + String.valueOf(port));
			return REG_STATUS.FAIL;
		}

		// session,proxy管理
		Session session = SessionManager.createSession(ctx.channel(), host, port);
		DeviceProxyManager.createProxy(session.getSessionId(), host, port);

		EventManager.publish(
				new Event(session, EventManager.EVENT_ON));
		
		return REG_STATUS.OK;
	}
	

	// 根据数据指纹fingerprint注册
	private byte[] regByFp(ChannelHandlerContext ctx, byte[] regBytes) {
		
		DeviceTypeEntity deviceType = DeviceAuthProxy.findDeviceType(regBytes);
		if (deviceType == null) {
			return null;
		}
		
		byte[] regInfoBytes = DeviceAuthProxy.makeRegInfo(regBytes, deviceType);
		
		return regInfoBytes;
	}
	
	private REG_STATUS regByToken(ChannelHandlerContext ctx, ByteBuf inReg) {

		logger.info("reginfo=" + inReg.toString(CharsetUtil.UTF_8));
		
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
		String regInfo = inReg.toString(CharsetUtil.UTF_8).trim();
		// 消除回车、换行, 冒号
		regInfo = regInfo.replaceAll("[\\n|\\r|:]", "");
		
		int splitIdx = regInfo.indexOf(REG_SPLIT);
		if ((splitIdx < 1) 
				|| !regInfo.endsWith(REG_END)) {
			
			regError(ctx, regInfo);
			return REG_STATUS.ERROR;
		}
		
		// 消除结尾符;
		regInfo = regInfo.replace(REG_END, "");
		String[] regInfoArr = regInfo.split(REG_SPLIT);
//		String deviceId = regInfo.substring(0, splitIdx);
//		String accessToken = regInfo.substring(splitIdx+1, regInfo.length()-1);
		String deviceId = regInfoArr[0];
		String accessToken = regInfoArr[1];
		
		// 设置心跳间隔
		if (regInfoArr.length > 2 
				&& NumericHelper.isIntegerPositive(regInfoArr[2])
				) {
			

			Integer heartBeat = Integer.valueOf(regInfoArr[2]);
			if (heartBeat > Global.HEART_BEAT_MAX) {
				heartBeat = Global.HEART_BEAT_TEN_MINUTES; // 超过24小时，修正为10分钟
			}
			
			if (heartBeat != Global.HEART_BEAT_DEFAULT ) {
				
				ctx.pipeline().remove(IdleStateHandler.class); // 删除默认10秒
				
//				if (heartBeat <= Global.HEART_BEAT_MAX) { // 超过24小时，不发送心跳
					ctx.pipeline().addFirst(
							new IdleStateHandler(0, 0, heartBeat*10,TimeUnit.SECONDS));
//				}
			}
		}
		
		// 鉴权
		if (!DeviceAuthProxy.checkToken(deviceId, accessToken)) {
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

		EventManager.publish(
				new Event(session, EventManager.EVENT_ON));
		
		return REG_STATUS.OK;
	}
	
	private void regError(ChannelHandlerContext ctx, String regInfo) {
		
		String outMsg = String.format("Auth failed, connection refused. data=[%s] ", regInfo);
		ctx.channel().writeAndFlush(ByteHelper.str2bb(outMsg));
		ctx.close();
		
		// 释放资源(交由 ModbusHandler 释放)
		// SessionManager.closeSession(ctx.channel().attr(Keys.SESSION_ID_KEY).get());
	}
	
	// host,port
	private Object[] getFromAddr(ChannelHandlerContext ctx) {
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String host = insocket.getHostName();
		int port = insocket.getPort();
		
		return new Object[]{host, port};
	}
	
}
