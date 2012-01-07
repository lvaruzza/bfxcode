package bfx.utils;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

public class TextUtils {
	public static String line() {
		return StringUtils.leftPad("", 60, "-");
	}
	public static String doubleLine() {
		return StringUtils.leftPad("", 60, "=");
	}
	public static String times(char c, int times) {
		char[] r = new char[times];
		Arrays.fill(r, c);
		return new String(r);
	}
	public static String times(String str, int times) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<times;i++)
			sb.append(str);
		return sb.toString();
	}	
}
