package cc.touchuan.jbus.common.helper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class ByteHelper {

	// ByteBuf to byte[]
	public static byte[] bb2bytes(ByteBuf bb) {
		
		
		ByteBuf copy = bb.copy();
		byte[] bytes = new byte[copy.readableBytes()];
		
		copy.readBytes(bytes);

		ReferenceCountUtil.release(copy);
		return bytes;
	}
	
	public static int toUnsignedInt(byte b) {
		return  (int) (b & 0xff);

	}
	
	public static ByteBuf bytes2bb(byte[] b) {
		return Unpooled.copiedBuffer(b);
		
	}
	

	// string to ByteBuf
	public static ByteBuf str2bb(String str) {
		 return Unpooled. copiedBuffer(str, CharsetUtil. UTF_8);
	}
	
	public static byte[] str2bytes(String str) {
		return str.getBytes(CharsetUtil. UTF_8);
	}
}
