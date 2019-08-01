package cc.touchuan.jbus.application;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.common.constant.Keys;
import cc.touchuan.jbus.handler.ModbusHandler;
import cc.touchuan.jbus.handler.RegistHandler;
import cc.touchuan.jbus.mqtt.MqttPool;
import cc.touchuan.jbus.plugin.jsonrpc.handler.JsonRpcHandler;
import cc.touchuan.jbus.session.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/*
 * Json-Rpc Server
 */
public class JrServer { 

	static Logger LOG = Logger.getLogger(JrServer.class);
	
	private final int port; 
	
	public JrServer( int port) { 
		this. port = port; 
	} 
	
	public static void main(String[] args) throws Exception { 
		
		
		int port = Integer. parseInt(args[0]); 
		
		new JrServer(port).start(); // 调用服务器的start()方法 
	}
 
	public void start() throws Exception { 
		
		EventLoopGroup group = new NioEventLoopGroup(); 
		
		try { 
			
			LOG.info("starting json-rpc server...");
			LOG.info("listen on port " + port);
			
			ServerBootstrap b = new ServerBootstrap(); 
			b. group( group).channel(NioServerSocketChannel.class)
				.localAddress( new InetSocketAddress( port)) 
				.childHandler( 
					new ChannelInitializer<Channel>(){ 
					
						@Override
						protected void initChannel(Channel ch) throws Exception { 
							ch.pipeline()
								.addLast("codec",new HttpServerCodec())
								.addLast("aggregator",new HttpObjectAggregator(512*1024))
								.addLast("logic", new JsonRpcHandler());
							

							ch.attr(Keys.CHANNEL_TYPE_KEY).set(Global.CHANNEL_TYPE_HTTP);
						}
					}); 
			
			ChannelFuture f = b.bind().sync(); // ❻ 异步地绑定服务器；调用sync()方法阻塞等待直到绑定完成 
			
			LOG.info("json-rpc server is running.");
			
			f.channel().closeFuture().sync(); // ❼ 获取Channel的CloseFuture，并且阻塞当前线程直到它完成 
		
		} finally { 
			group.shutdownGracefully().sync(); // ❽ 关闭EventLoopGroup，释放所有的资源 
		} 
	} 
}


