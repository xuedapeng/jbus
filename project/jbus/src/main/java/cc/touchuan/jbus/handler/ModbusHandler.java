package cc.touchuan.jbus.handler;


import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.constant.Keys;
import cc.touchuan.jbus.common.helper.ByteHelper;
import cc.touchuan.jbus.common.helper.HexHelper;
import cc.touchuan.jbus.proxy.DeviceProxy;
import cc.touchuan.jbus.proxy.DeviceProxyManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import io.netty.channel.ChannelHandler.Sharable;

@Sharable 
public class ModbusHandler extends ChannelInboundHandlerAdapter {

	static Logger logger = Logger.getLogger(ModbusHandler.class);

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		logger.info("channelActive");
		
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		ByteBuf in = (ByteBuf) msg;
		byte[] data = ByteHelper.bb2bytes(in);

		logger.info("channelRead:" + HexHelper.bytesToHexString(data));
		
		// 透传
		String sessionId = ctx.channel().attr(Keys.SESSION_ID_KEY).get();
		DeviceProxy proxy = DeviceProxyManager.findProxy(sessionId);
		proxy.sendToCtrl(data);
		
	} 
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		
		ctx.writeAndFlush("unknown error.");
		ctx.close();
		
		logger.info("", cause);
	}

}
