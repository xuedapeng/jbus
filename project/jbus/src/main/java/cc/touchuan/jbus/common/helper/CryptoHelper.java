package cc.touchuan.jbus.common.helper;

import java.util.UUID;

public class CryptoHelper {

	public static String genUUID() {
		String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
	    return uuid;
	}
}
