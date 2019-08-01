package cc.touchuan.jbus.common.helper;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class NumericHelper {
	public static boolean isInteger(String str) {  
		  
		if (StringUtils.isEmpty(str)) {
			  return false;
		}
		  
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]+$");  
		return pattern.matcher(str).matches();  
	}

	public static boolean isIntegerPositive(String str) {  
		  
		if (StringUtils.isEmpty(str)) {
			  return false;
		}
		  
		Pattern pattern = Pattern.compile("^[1-9][0-9]*$");  
		return pattern.matcher(str).matches();  
	}
	
	public static boolean isEmail(String str) {

		if (StringUtils.isEmpty(str)) {
			  return false;
		}
		  
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
		return pattern.matcher(str).matches();  
	}
}
