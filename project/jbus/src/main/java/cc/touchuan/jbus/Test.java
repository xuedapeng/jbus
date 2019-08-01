package cc.touchuan.jbus;

import cc.touchuan.jbus.common.helper.NumericHelper;
import io.netty.util.CharsetUtil;

public class Test {

	public static void main(String[] args) {
		
		String s = new String(new byte[]{(byte)0x88, 0x36}, CharsetUtil.UTF_8);
		
		System.out.println(s);

	}

}
