package bfx.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ByteBuffer {
	private List<byte[]> storage;
	private int size;
	
	public ByteBuffer() {
		storage = new LinkedList<byte[]>();	
	}
	
	public void insert(byte[] vec,int start,int len) {
		storage.add(Arrays.copyOfRange(vec, start, start+len));
		size+=len;
	}

	public void append(byte[] vec) {
		insert(vec,0,vec.length);
	}
	
	public byte[] get() {
		byte[] ret = new byte[size];
		int pos = 0;
		for(byte[] s:storage) {
			for(byte x: s)
				ret[pos++] = x;
		}
		return ret;
	}
}
