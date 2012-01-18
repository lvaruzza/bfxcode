package bfx.utils;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

/**
 * Text Utils
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class TextUtils {
	public static int lineSize = 80;
	
	/**
	 * Return a ASCII art line.
	 * 
	 * @return A string with lineSize '-'
	 */
	public static String line() {
		return StringUtils.leftPad("", lineSize, "-");
	}
	
	/**
	 * Return a ascii art double line.
	 * 
	 * @return A string with lineSize '='
	 */
	public static String doubleLine() {
		return StringUtils.leftPad("", lineSize, "=");
	}
	
	/**
	 * Return a string with n times the character c
	 * 
	 * @param c character
	 * @param n number of copies of c
	 * @return A new string with n times c
	 */
	public static String times(char c, int n) {
		char[] r = new char[n];
		Arrays.fill(r, c);
		return new String(r);
	}
	
	/**
	 * Return a string with n times the string str
	 * 
	 * @param str string
	 * @param n number of copies of str
	 * @return A new string with n times str
	 */
	public static String times(String str, int n) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<n;i++)
			sb.append(str);
		return sb.toString();
	}

	public static String banner(String message) {
		return doubleLine() + "\n" + message + "\n" + doubleLine() + "\n";
	}	
}
