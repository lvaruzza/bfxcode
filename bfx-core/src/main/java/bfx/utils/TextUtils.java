package bfx.utils;

import org.apache.commons.lang.StringUtils;

public class TextUtils {
	public static String line() {
		return StringUtils.leftPad("", 60, "-");
	}
	public static String doubleLine() {
		return StringUtils.leftPad("", 60, "=");
	}
}
