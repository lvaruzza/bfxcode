package bfx.utils;

public class BFXArrayUtils {

	// Parse Longs

	public static long[] parseLongs(String[] lst, int start, int end) {
		long[] r = new long[end - start];

		for (int i = start; i < end; i++)
			r[i - start] = Long.parseLong(lst[i]);
		return r;
	}

	public static long[] parseLongs(String[] lst, int init) {
		return parseLongs(lst, init, lst.length);
	}

	// Parse Ints

	public static int[] parseInts(String[] lst, int start, int end) {
		int[] r = new int[end - start];

		for (int i = start; i < end; i++)
			r[i - start] = Integer.parseInt(lst[i]);
		return r;
	}

	public static int[] parseInts(String[] lst, int init) {
		return parseInts(lst, init, lst.length);
	}

	public static long[] grow(long[] array, int minSize) {
		if (array.length < minSize) {
			long[] newArray = new long[Math.max(array.length << 1, minSize)];
			System.arraycopy(array, 0, newArray, 0, array.length);
			return newArray;
		} else
			return array;
	}

	public static long[] grow(long[] array) {
		return grow(array, 1 + array.length);
	}
}
