package cc.touchuan.jbus.handler;

import cc.touchuan.jbus.common.constant.Keys;
import cc.touchuan.jbus.session.Session;
import cc.touchuan.jbus.session.SessionManager;
import cc.touchuan.jbus.session.Session.PROT_TYPE;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WsTextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

		Session session = SessionManager.findBySessionId(ctx.channel().attr(Keys.SESSION_ID_KEY).get());
		session.setProtType(PROT_TYPE.WS_TXT);
		
		ctx.fireChannelRead(msg.content());
		
	}


}
