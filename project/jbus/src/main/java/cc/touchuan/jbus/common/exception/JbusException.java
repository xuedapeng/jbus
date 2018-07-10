package cc.touchuan.jbus.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class JbusException extends RuntimeException {

	public JbusException(Throwable cause) {
		super(cause);
	}

	public JbusException(String msg) {
		super(msg);
	}


    public static String trace(Throwable e) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
    	return sw.toString();	
    }
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9012823218729401357L;

}
