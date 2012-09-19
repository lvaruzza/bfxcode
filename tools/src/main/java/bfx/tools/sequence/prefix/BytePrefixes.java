package bfx.tools.sequence.prefix;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import scala.actors.threadpool.Arrays;

import com.google.common.primitives.UnsignedBytes;

public class BytePrefixes implements Iterable<Map.Entry<byte[],Long>> {
	private Map<byte[],Long> prefix;
	
	public BytePrefixes() {
		prefix = new TreeMap<byte[],Long>(UnsignedBytes.lexicographicalComparator());
	}
	
	public void add(byte[] x) {
		add(x,0,x.length);
	}

	public void add(byte[] x,int start,int end) {
		
		for(int i=start+1;i<=end;i++) {
			byte[] key = Arrays.copyOfRange(x, start, i);
			long count = 1;
			if (prefix.containsKey(key))
				count += prefix.get(key);
			prefix.put(key, count);
		}
	}
	
	public Iterator<Map.Entry<byte[],Long>> iterator() {
		return prefix.entrySet().iterator();
	}
}
