package bfx.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Mutable startructure to build an byte array. 
 * 
 * This ByteBuffer supports the append operation to add bytes and the get operation to get the resulting
 * array. 
 * 
 * Each byte array will be put in a linked list. The get operation will allocate a new array with the 
 * result size and copy all the bytes to this new array.
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class ByteBuffer {
	private LinkedList<byte[]> storage;
	private int size;
	
	/**
	 * Construct a nw ByteBuffer
	 */
	public ByteBuffer() {
		storage = new LinkedList<byte[]>();	
	}
	
	/**
	 * Add bytes to the buffer.
	 * 
	 * @param vec byte array
	 * @param start start position
	 * @param len length of selected region
	 */
	public void append(byte[] vec,int start,int len) {
		storage.push(Arrays.copyOfRange(vec, start, start+len));
		size+=len;
	}

	/**
	 * Add bytes to buffer
	 * 
	 * @param vec byte array
	 */
	public void append(byte[] vec) {
		append(vec,0,vec.length);
	}
	
	/**
	 * Return the buffer contents in a new allocated byte array.
	 * 
	 * @return buffer contents.
	 * 
	 */
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

	/**
	 * Number of bytes in the buffer.
	 * 
	 * @return Bytes in the buffer
	 */
	public int length() {
		return size;
	}
}
