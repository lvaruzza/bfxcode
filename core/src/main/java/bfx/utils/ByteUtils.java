package bfx.utils;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;

public class ByteUtils {
	public static void printBytes(PrintStream out,byte[] bs, int start, int size) {
		for(int i=start;i<start+size;i++) {
			out.print(Byte.toString(bs[i]));
			out.write(' ');
		}		
	}
	public static void printBytes(Writer out,byte[] bs, int start, int size) throws IOException {
		for(int i=start;i<start+size;i++) {
			out.write(Byte.toString(bs[i]));
			out.write(' ');
		}		
	}
}
