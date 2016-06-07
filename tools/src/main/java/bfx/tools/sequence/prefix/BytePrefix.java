package bfx.tools.sequence.prefix;

import java.util.Iterator;
import java.util.Map;

public interface BytePrefix extends Iterable<Map.Entry<byte[],Long>> {

	public abstract void add(byte[] x);

	public abstract void add(byte[] x, int start, int end);

	public abstract Iterator<Map.Entry<byte[], Long>> iterator();

}