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
	public static String times(char c, int size) {
		char[] r = new char[size];
		Arrays.fill(r, c);
		return new String(r);
	}
}
