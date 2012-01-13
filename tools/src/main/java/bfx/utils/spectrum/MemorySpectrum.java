package bfx.utils.spectrum;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import bfx.utils.ByteUtils;
import bfx.utils.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public class MemorySpectrum extends Spectrum {
	private TreeMap<byte[],Long> map;
	
	public MemorySpectrum(int k) {
		super(k);
		map = new TreeMap<byte[],Long>(new ByteUtils.BytesComparator());
	}

	@Override
	protected void add1(byte[] seq) {	
		if (map.containsKey(seq))
			map.put(seq, map.get(seq)+1);
		else
			map.put(seq,0l);
	}

	@Override
	public boolean member(byte[] seq) {
		return map.containsKey(seq);
	}

	@Override
	public long getCount(byte[] seq) {
		if (map.containsKey(seq))
			return map.get(seq);
		else 
			return 0;
	}

	@Override
	public Iterator<Pair<byte[], Long>> iterator() {
		return Iterators.transform(map.entrySet().iterator(), new Function<Entry<byte[],Long>,Pair<byte[],Long>>() {
			@Override public Pair<byte[], Long> apply(Entry<byte[], Long> e) {
				return new Pair<byte[], Long>(e.getKey(),e.getValue());
			}			
		});
	}
}
