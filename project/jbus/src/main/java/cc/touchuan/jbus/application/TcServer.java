package cc.touchuan.jbus.application;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import cc.touchuan.jbus.handler.ModbusHandler;
import cc.touchuan.jbus.handler.RegistHandler;
import cc.touchuan.jbus.mqtt.MqttPool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TcServer { 

	static Logger LOG = Logger.getLogger(TcServer.class);
	
	private final int port; 
	
	public TcServer( int port) { 
		this. port = port; 
	} 
	
	public static void main(String[] args) throws Exception { 
		
		
		int port = Integer. parseInt(args[0]); 
		
		new TcServer(port).start(); // 调用服务器的start()方法 
	}
 
	public void start() throws Exception { 

		// 初始化mqtt pool
		MqttPool.initialize();
		
		EventLoopGroup group = new NioEventLoopGroup(); 
		
		try { 
			
			LOG.info("starting touchuan server...");
			LOG.info("listen on port " + port);
			
			ServerBootstrap b = new ServerBootstrap(); 
			b. group( group).channel(NioServerSocketChannel.class)
				.localAddress( new InetSocketAddress( port)) 
				.childHandler( 
					new ChannelInitializer(){ 
					
						protected void initChannel(Channel ch) throws Exception { 
							ch.pipeline()
								.addLast(new RegistHandler())
								.addLast(new ModbusHandler());
						}
					}); 
			
			ChannelFuture f = b.bind().sync(); // ❻ 异步地绑定服务器；调用sync()方法阻塞等待直到绑定完成 
			
			LOG.info("touchuan server is running.");
			
			f.channel().closeFuture().sync(); // ❼ 获取Channel的CloseFuture，并且阻塞当前线程直到它完成 
		
		} finally { 
			group.shutdownGracefully().sync(); // ❽ 关闭EventLoopGroup，释放所有的资源 
		} 
	} 
}


