package bfx.utils;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Comparator;

/**
 * Byte Utils.
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class ByteUtils {
	
	/**
	 * Print bytes in the format "b1 b2 b3..."
	 * 
	 * @param out Output PrintStream
	 * @param bs byte array
	 * @param start start position
	 * @param length
	 */
	public static void printBytes(PrintStream out,byte[] bs, int start, int length) {
		for(int i=start;i<start+length;i++) {
			out.print(Byte.toString(bs[i]));
			out.write(' ');
		}		
	}
	
	/**
	 * Print bytes in the format "b1 b2 b3..."
	 * 
	 * @param out Output Writer
	 * @param bs byte array
	 * @param start start position
	 * @param length
	 */
	public static void printBytes(Writer out,byte[] bs, int start, int length) throws IOException {
		for(int i=start;i<start+length;i++) {
			out.write(Byte.toString(bs[i]));
			out.write(' ');
		}		
	}
	
	public static class BytesComparator implements Comparator<byte[]> {
		@Override
		public int compare(byte[] as, byte[] bs) {
			for(int i=0;i<Math.min(as.length,bs.length);i++) {
				if (as[i] < bs[i]) return 1;
				if (as[i] > bs[i]) return -1;
			}
			if(as.length < bs.length) return 1;
			if(as.length > bs.length) return -1;
			return 0;
		}
		
	}
}
