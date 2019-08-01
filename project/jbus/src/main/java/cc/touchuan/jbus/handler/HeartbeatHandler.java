package cc.touchuan.jbus.handler;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.application.Global;
import cc.touchuan.jbus.common.constant.Keys;
import cc.touchuan.jbus.common.helper.ByteHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

	static Logger logger = Logger.getLogger(HeartbeatHandler.class);
	
	private static final ByteBuf HEARTBEAT_SEQUENCE= 
			Unpooled.unreleasableBuffer(ByteHelper.bytes2bb(new byte[]{0x00}));
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx,
			Object evt) throws Exception {
		
		if (evt instanceof IdleStateEvent) {
			
//			Integer hb = Integer.valueOf(ctx.channel().attr(Keys.HEART_INTERVAL_KEY).get());
//			if (hb > Global.HEART_BEAT_DEFAULT) {
//				// 根据间隔设置，判断是否发送心跳
//				
//			}
			
			logger.info("send heartbeat:0x00,sessionId=" + ctx.channel().attr(Keys.SESSION_ID_KEY));
			ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
				.addListener(
					ChannelFutureListener.CLOSE_ON_FAILURE);
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
	
}
