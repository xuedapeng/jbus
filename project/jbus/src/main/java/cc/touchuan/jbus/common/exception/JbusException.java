package cc.touchuan.jbus.common.exception;

public class JbusException extends RuntimeException {

	public JbusException(Throwable cause) {
		super(cause);
	}

	public JbusException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 9012823218729401357L;

}
