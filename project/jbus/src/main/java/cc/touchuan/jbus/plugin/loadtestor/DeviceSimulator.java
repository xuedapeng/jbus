package cc.touchuan.jbus.plugin.loadtestor;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.touchuan.jbus.common.helper.ByteHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class DeviceSimulator {
	
	String host;
	int port;
	
	public static int maxDeviceId = 0;
	public  String deviceId;
	public  Channel channel;

	
	public DeviceSimulator(String host, int port) {
		this.host = host;
		this.port = port;
	}
	

	public void start() throws Exception { 
		EventLoopGroup group = new NioEventLoopGroup(); 
		try { 
			Bootstrap b = new Bootstrap();
			b.group( group).channel(NioSocketChannel.class) 
				.remoteAddress(new InetSocketAddress(host, port)) 
				.handler(
						new ChannelInitializer<SocketChannel>() { 
							@Override 
							public void initChannel(SocketChannel ch) throws Exception { 
								ch. pipeline(). addLast( new DeviceSimulatorHandler()); 
							} 
						}); 

			ChannelFuture cf = b.connect().sync(); // 连接到远程节点，阻塞等待直到连接完成 
			cf.channel().closeFuture().sync(); //阻塞，直到 Channel 关闭 
			
	        
		} finally { 
			group. shutdownGracefully(). sync(); //关闭线程池并且释放所有的资源 
		} 
	} 
	
	class DeviceSimulatorHandler extends SimpleChannelInboundHandler<ByteBuf> {

		String _localHost;
		int _localPort;
//		String _devId;
		
		@Override 
		public void channelActive(ChannelHandlerContext ctx) {

			deviceId = genDeviceId();
			
			// 获取客户端地址
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().localAddress();
			_localHost = insocket.getHostName();
			_localPort = insocket.getPort();
			
			//_devId = _localHost + ":" + _localPort;
			String regInfo = String.format("REG:%s,%s;", deviceId, deviceId);
			ctx.writeAndFlush(ByteHelper.str2bb(regInfo));
			
			channel = ctx.channel();
			
			System.out.println(String.format("active devId=%s", deviceId));
		}
		
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
			
			System.out.println(deviceId + " recieve :" + msg.toString(CharsetUtil.UTF_8));

			String orgMsg = msg.toString(CharsetUtil.UTF_8);
			if (!orgMsg.startsWith("RE:")) {
				String dat = String.format("RE: %s [%s] ", msg.toString(CharsetUtil.UTF_8), new Date().toString());

				ctx.writeAndFlush(ByteHelper.str2bb(dat));
				System.out.println(deviceId + " send data");
			}

			
		}

		@Override 
		public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause) { 
			
			cause. printStackTrace(); 
			ctx.close(); 
			
			channel = null;
		}
		@Override
		public void channelInactive(ChannelHandlerContext ctx) {

			channel = null;
			System.out.println(String.format("inactive devId=%s", deviceId));
		}
		
	}
	
	private synchronized static String genDeviceId() {
		
		maxDeviceId++;
		return "dev_" + maxDeviceId;
	}

}
