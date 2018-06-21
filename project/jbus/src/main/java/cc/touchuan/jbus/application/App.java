package cc.touchuan.jbus.application;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
		int port = 8082;
		
		if (args. length != 1) { 
			System.err.println( "Usage: " + TcServer.class.getSimpleName() + " "); 
			
		} else {
			port = Integer. parseInt(args[0]); // 设置端口值（如果端口参数的格式不正确，则抛出一个NumberFormatException） 
		}
		
		try {
			
			new TcServer( port).start(); // 调用服务器的start()方法 
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} 
    }
}
