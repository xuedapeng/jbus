package cc.touchuan.jbus.plugin.jsonrpc.handler;

import java.util.HashMap;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.exception.JbusException;
import cc.touchuan.jbus.common.helper.ByteHelper;
import cc.touchuan.jbus.common.helper.JsonBuilder;
import cc.touchuan.jbus.plugin.jsonrpc.logic.HttpStaticResource;
import cc.touchuan.jbus.plugin.jsonrpc.logic.LogicRouter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class JsonRpcHandler  extends SimpleChannelInboundHandler<FullHttpRequest>{
	
	static final String URI_JSON_RPC = "/jsonrpc";
	static final String URI_ADMIN = "/admin";

	static Logger logger = Logger.getLogger(JsonRpcHandler.class);
	
	FullHttpRequest mRequest;

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		logger.info("channelActive");
		
	}	

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

		logger.info("channelRead: content=" + request.content().toString(CharsetUtil.UTF_8));
		logger.info("channelRead: method=" + request.method());
		logger.info("channelRead: uri=" + request.uri());
		
		mRequest = request;
		
		String uri = request.uri();

		if (uri.startsWith(URI_ADMIN)) {
			HttpStaticResource.process(ctx, request);
			return;
		}
		// 校验地址
		if (!uri.startsWith(URI_JSON_RPC)) {
			FullHttpResponse res = this.makeResponse(
					JsonBuilder.build()
					.add("status", "-1")
					.add("msg", "无效地址: "+uri)
					.toString(),
					request);
			
			ctx.writeAndFlush(res);
			
			return;
		}
		
		String method = request.method().name();
		

		if (HttpMethod.OPTIONS.name().equals(method)) {
			ctx.writeAndFlush(this.makeResponse("", request));
			
			return;
		}

		// 处理get(忽略参数)
		if (HttpMethod.GET.name().equals(method.toUpperCase())) {
			String rpcMethod =  uri.substring(URI_JSON_RPC.length()+1);
			String rpcParam = JsonBuilder.build()
					.add("method", rpcMethod)
					.add("data", new HashMap<>()).toString();
			
			String rpcResponse = LogicRouter.process(rpcParam);
			ctx.writeAndFlush(this.makeResponse(rpcResponse, request));
			
			return;
			
		}
		
		// 处理post

		if (HttpMethod.POST.name().equals(method.toUpperCase())) {
			
			String rpcParam = request.content().toString(CharsetUtil.UTF_8);
			
			String rpcResponse = LogicRouter.process(rpcParam);
			ctx.writeAndFlush(this.makeResponse(rpcResponse, request));
			
			return;
			
		}
		
		
	}
	
	@Override 
	public void channelReadComplete(ChannelHandlerContext ctx) { 

	} 
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) {

		logger.info("channelInactive. ");
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

		logger.info("", cause);
		
		String res = JsonBuilder.build()
				.add("status", "-1")
				.add("msg", JbusException.trace(cause))
				.toString();
		
		ctx.writeAndFlush(this.makeResponse(res, mRequest));
		ctx.close();
		
	}

	/*
	 * 
	 */
	private FullHttpResponse makeResponse(String msg, FullHttpRequest request) {

		FullHttpResponse res = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1, 
				HttpResponseStatus.OK,
				ByteHelper.str2bb(msg));
		
		res.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
		res.headers().set(HttpHeaderNames.CONTENT_LENGTH, res.content().readableBytes());
		
		res.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*"); 
		res.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"Origin, X-Requested-With, Content-Type, Accept");
		res.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,"GET,POST,OPTIONS");
		res.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
		
		if (HttpUtil.isKeepAlive(request)) {
            res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
		
		logger.info("res.headers=" + res.headers().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN));
		logger.info("res.content=" + res.content().toString(CharsetUtil.UTF_8));
		
		return res;
	}
	
	


}
