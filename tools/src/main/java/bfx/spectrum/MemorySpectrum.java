package bfx.spectrum;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import bfx.exceptions.FileProcessingIOException;
import bfx.utils.ByteUtils;
import bfx.utils.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public class MemorySpectrum extends Spectrum {
	private static Logger log = Logger.getLogger(MemorySpectrum.class);
	
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
		else {
			map.put(seq,1l);
			nkmers++;
		}
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

	private void loadKmers(DataInputStream dis) throws IOException {
		int i =0;
		for(;i<nkmers;i++) {
			byte[] kmer=new byte[k];
			dis.read(kmer);
			long count = dis.readLong();
			log.debug(String.format("L: %s\t%d",new String(kmer),count));
			map.put(kmer, count);
			log.debug(String.format("Map size = %d",map.size()));
		}
		log.debug(String.format("Loaded kmers = %d",i));
	}
	
	public void save(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		writeHeader(dos);
		for(Pair<byte[],Long> pair: this) {
			dos.write(pair.fst);
			dos.writeLong(pair.snd);
		}
		out.close();
	};
	
	
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
		log.info(String.format("Loaded Spectrum k = %d",spc.k));
		log.info(String.format("Number of kmers = %d",spc.nkmers));
		spc.loadKmers(dis);
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
