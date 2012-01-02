package bfx.utils;

public class BFXArrayUtils {
	
	// Parse Longs
	
	public static long[] parseLongs(String[] lst,int start,int end) {
		long[] r=new long[end-start];
		
		for(int i=start;i<end;i++)
			r[i-start] = Long.parseLong(lst[i]);
		return r;
	}

	public static long[] parseLongs(String[] lst, int init) {
		return parseLongs(lst,init,lst.length);
	}
	
	// Parse Ints
	
	public static int[] parseInts(String[] lst,int start,int end) {
		int[] r=new int[end-start];
		
		for(int i=start;i<end;i++)
			r[i-start] = Integer.parseInt(lst[i]);
		return r;
	}

	public static int[] parseInts(String[] lst, int init) {
		return parseInts(lst,init,lst.length);
	}
	
}
