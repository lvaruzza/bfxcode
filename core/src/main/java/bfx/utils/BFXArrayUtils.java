package bfx.utils;

/**
 * Array Utils
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class BFXArrayUtils {
	
	/**
	 * Convert an array of string into a array of longs
	 * 
	 * @param lst Array of strings
	 * @param start first element
	 * @param end last element
	 * @return array of longs
	 */
	public static long[] parseLongs(String[] lst,int start,int end) {
		long[] r=new long[end-start];
		
		for(int i=start;i<end;i++)
			r[i-start] = Long.parseLong(lst[i]);
		return r;
	}

	/**
	 * Convert an array of string into a array of longs
	 * 
	 * @param lst Array of strings
	 * @param start first element
	 * @return array of longs
	 */
	public static long[] parseLongs(String[] lst, int start) {
		return parseLongs(lst,start,lst.length);
	}
	
	/**
	 * Convert an array of string into a array of ints
	 * 
	 * @param lst Array of strings
	 * @param start first element
	 * @param end last element
	 * @return array of ints
	 */
	public static int[] parseInts(String[] lst,int start,int end) {
		int[] r=new int[end-start];
		
		for(int i=start;i<end;i++)
			r[i-start] = Integer.parseInt(lst[i]);
		return r;
	}

	/**
	 * Convert an array of string into a array of ints
	 * 
	 * @param lst Array of strings
	 * @param start first element
	 * @return array of ints
	 */
	public static int[] parseInts(String[] lst, int start) {
		return parseInts(lst,start,lst.length);
	}
	
}
