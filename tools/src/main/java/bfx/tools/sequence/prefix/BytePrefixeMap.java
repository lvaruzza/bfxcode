package bfx.tools.sequence.prefix;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.primitives.UnsignedBytes;

public class BytePrefixeMap implements BytePrefix {
	private Map<byte[],Long> prefix;
	
	public BytePrefixeMap() {
		prefix = new TreeMap<byte[],Long>(UnsignedBytes.lexicographicalComparator());
	}
	
	/* (non-Javadoc)
	 * @see bfx.tools.sequence.prefix.BytePrefix#add(byte[])
	 */
	@Override
	public void add(byte[] x) {
		add(x,0,x.length);
	}

	/* (non-Javadoc)
	 * @see bfx.tools.sequence.prefix.BytePrefix#add(byte[], int, int)
	 */
	@Override
	public void add(byte[] x,int start,int end) {
		
		for(int i=start+1;i<=end;i++) {
			byte[] key = Arrays.copyOfRange(x, start, i);
			long count = 1;
			if (prefix.containsKey(key))
				count += prefix.get(key);
			prefix.put(key, count);
		}
	}
	
	/* (non-Javadoc)
	 * @see bfx.tools.sequence.prefix.BytePrefix#iterator()
	 */
	@Override
	public Iterator<Map.Entry<byte[],Long>> iterator() {
		return prefix.entrySet().iterator();
	}
}
