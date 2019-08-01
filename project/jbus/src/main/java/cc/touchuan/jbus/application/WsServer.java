package cc.touchuan.jbus.application;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.constant.Keys;
import cc.touchuan.jbus.handler.HeartbeatHandler;
import cc.touchuan.jbus.handler.ModbusHandler;
import cc.touchuan.jbus.handler.RegistHandler;
import cc.touchuan.jbus.handler.WsBinaryFrameHandler;
import cc.touchuan.jbus.handler.WsTextFrameHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class WsServer { 

	static Logger LOG = Logger.getLogger(WsServer.class);
	
	private final int port; 
	
	public WsServer( int port) { 
		this. port = port; 
	} 
	
	public static void main(String[] args) throws Exception { 
		
		
		int port = Integer. parseInt(args[0]); 
		
		new WsServer(port).start(); // 调用服务器的start()方法 
	}
 
	public void start() throws Exception { 

		EventLoopGroup group = new NioEventLoopGroup(); 
		
		try { 
			
			LOG.info("starting websocket server...");
			LOG.info("listen on port " + port);
			
			ServerBootstrap b = new ServerBootstrap(); 
			b. group( group).channel(NioServerSocketChannel.class)
				.localAddress( new InetSocketAddress( port)) 
				.childHandler( 
					new ChannelInitializer<Channel>(){ 
					
						protected void initChannel(Channel ch) throws Exception { 
							ch.pipeline()
								.addLast(new IdleStateHandler(0, 0, 10,TimeUnit.SECONDS))
								.addLast(new HeartbeatHandler())
								.addLast(new HttpServerCodec(),
										new HttpObjectAggregator(65536),
										new WebSocketServerProtocolHandler("/"),
										new WsBinaryFrameHandler(),
										new WsTextFrameHandler()
										)
								.addLast(new RegistHandler())
								.addLast(new ModbusHandler());
							
							ch.attr(Keys.CHANNEL_TYPE_KEY).set(Global.CHANNEL_TYPE_WS);
						}
					}); 
			
			ChannelFuture f = b.bind().sync(); // ❻ 异步地绑定服务器；调用sync()方法阻塞等待直到绑定完成 
			
			LOG.info("websocket server is running.");
			
			f.channel().closeFuture().sync(); // ❼ 获取Channel的CloseFuture，并且阻塞当前线程直到它完成 
		
		} finally { 
			group.shutdownGracefully().sync(); // ❽ 关闭EventLoopGroup，释放所有的资源 
		} 
	} 
}


