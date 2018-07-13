package cc.touchuan.jbus.plugin.jsonrpc.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.conf.ZSystemConfig;
import cc.touchuan.jbus.common.helper.ByteHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpStaticResource {

	static Logger logger = Logger.getLogger(HttpStaticResource.class);
	
	static String DISK_BASE = ZSystemConfig.getSystemConfigPath() + "/admin";
	
	public static void process(ChannelHandlerContext ctx, FullHttpRequest request) {

		String uri = request.uri();
		
		Path path = Paths.get(DISK_BASE , uri.replace("/admin", ""));
		
		if (!Files.exists(path)) {


			FullHttpResponse res = makeResponse(ByteHelper.str2bb("404 not found."), 
					HttpResponseStatus.NOT_FOUND, 
					"text/plain", 
					request);
			ctx.writeAndFlush(res);
			return;
		}
		
		byte[] fileContents=null;
		try {
			fileContents = Files.readAllBytes(path);

			
			FullHttpResponse res = makeResponse(ByteHelper.bytes2bb(fileContents), 
					HttpResponseStatus.OK,
					probeContentType(path),
					request);
					
			ctx.writeAndFlush(res);
			return;
			
			
		} catch (IOException e) {
			logger.error("", e);

			FullHttpResponse res = makeResponse(ByteHelper.str2bb(""), 
					HttpResponseStatus.INTERNAL_SERVER_ERROR, 
					"text/plain", 
					request);
			ctx.writeAndFlush(res);
			return;
		}
		
		

	}

	private static String probeContentType(Path path) {
		
		String fileName=path.toFile().getName();  
		
		if (fileName.endsWith(".html")) {
			return "text/html";
		}
		return null;
	}

	private static FullHttpResponse makeResponse(ByteBuf bb, 
			HttpResponseStatus status, 
			String contentType, 
			FullHttpRequest request) {

		FullHttpResponse res = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1, 
				status,
				bb);

		if (contentType != null) {
			res.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
		}
		res.headers().set(HttpHeaderNames.CONTENT_LENGTH, res.content().readableBytes());
		res.headers().set(HttpHeaderNames.CACHE_CONTROL, "public, max-age=604800, s-maxage=43200");
		
		
		if (HttpUtil.isKeepAlive(request)) {
            res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
		
		return res;
	}
	
	

}
