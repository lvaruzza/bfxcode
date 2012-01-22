package bfx.spectrum;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import bfx.exceptions.FileProcessingIOException;
import bfx.spectrum.SpectrumIO.SpectrumHeader;
import bfx.utils.ByteUtils;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public class MemorySpectrum extends Spectrum {
	private static Logger log = Logger.getLogger(MemorySpectrum.class);
	
	private TreeMap<byte[],Long> map = new TreeMap<byte[],Long>(new ByteUtils.BytesComparator());

	protected MemorySpectrum() {};

	protected MemorySpectrum(int k,long nkmers) {
		super(k,nkmers);
	};
	
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
	public Iterator<Kmer> iterator() {
		return Iterators.transform(map.entrySet().iterator(), new Function<Entry<byte[],Long>,Kmer>() {
			@Override public Kmer apply(Entry<byte[], Long> e) {
				return new Kmer(e.getKey(),e.getValue());
			}			
		});
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
		SpectrumHeader header = SpectrumIO.readHeader(dis);
		spc.k=header.k;
		spc.nkmers = header.nkmers;
		
		log.info(String.format("Loaded Spectrum k = %d",spc.k));
		log.info(String.format("Number of kmers = %d",spc.nkmers));
		spc.loadKmers(dis);
		return spc;
	}
	
	private void loadKmers(DataInputStream dis) throws IOException {
		int i =0;
		for(;i<nkmers;i++) {
			byte[] kmer=new byte[k];
			dis.read(kmer);
			long count = dis.readLong();
			//log.debug(String.format("L: %s\t%d",new String(kmer),count));
			map.put(kmer, count);
			//log.debug(String.format("Map size = %d",map.size()));
		}
		log.debug(String.format("Loaded kmers = %d",i));
	}

	protected void setMap(TreeMap<byte[],Long> map) {
		this.map = map;
	}
}
