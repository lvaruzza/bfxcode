package bfx.spectrum;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import bfx.exceptions.FileProcessingIOException;
import bfx.utils.ByteUtils;
import bfx.utils.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public class MemorySpectrum extends Spectrum {
	private TreeMap<byte[],Long> map = new TreeMap<byte[],Long>(new ByteUtils.BytesComparator());
	
	public MemorySpectrum(int k) {
		super(k);
	}
	
	protected MemorySpectrum() {
		super();
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

	private void load(DataInputStream dis) throws IOException {
		byte[] kmer=new byte[k];
		
		while(dis.read(kmer)!=k) {
			long count = dis.readLong();
			map.put(kmer, count);
		}		
	}
	
	public static Spectrum load(String filename) throws IOException {
		try {
			return load(new FileInputStream(filename));
		} catch(Exception e) {
			throw new FileProcessingIOException(e,new File(filename));
		}
	}
	public static Spectrum load(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);
		MemorySpectrum spc = new MemorySpectrum();
		spc.readHeader(dis);
		spc.load(dis);
		return spc;
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
