package bfx.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class ByteBuffer {
	private LinkedList<byte[]> storage;
	private int size;
	
	public ByteBuffer() {
		storage = new LinkedList<byte[]>();	
	}
	
	public void append(byte[] vec,int start,int len) {
		storage.push(Arrays.copyOfRange(vec, start, start+len));
		size+=len;
	}

	public void append(byte[] vec) {
		append(vec,0,vec.length);
	}
	
	public byte[] get() {
		byte[] ret = new byte[size];
		int pos = 0;
		Iterator<byte[]> revit = storage.descendingIterator();
		
		while(revit.hasNext()) {
			byte[] s = revit.next();
			for(byte x: s)
				ret[pos++] = x;
		}
		return ret;
	}

	public int length() {
		return size;
	}
}
