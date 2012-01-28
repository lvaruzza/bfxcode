package bfx.utils;

import java.text.DecimalFormat;
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

	private static String[] bytesUnits={" bytes"," kb"," Mb"," Gb"," Tb"};
	private static DecimalFormat bytesDf = new DecimalFormat("#.##"); 
	public static String formatBytes(double bytes) {
		//System.out.println("bytes = " + bytes);
		
		if (bytes == 0) return "0 bytes";
		if (bytes == 1) return "1 byte";
		int idx = (int)(Math.log(bytes)/Math.log(2))/10;
		if (idx > bytesUnits.length-1) idx=bytesUnits.length-1;
		//System.out.println("idx = " + idx);
		double x=bytes/Math.pow(2, idx*10);
		return bytesDf.format(x) + bytesUnits[idx];
	}	
}
