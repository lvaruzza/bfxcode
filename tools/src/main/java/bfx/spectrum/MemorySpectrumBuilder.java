package bfx.spectrum;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import bfx.utils.ByteUtils;
import bfx.utils.RuntimeUtils;
import bfx.utils.TextUtils;

public class MemorySpectrumBuilder extends SpectrumBuilder  {
	private static Logger log = Logger.getLogger(MemorySpectrumBuilder.class);
	
	private TreeMap<byte[],Long> map = new TreeMap<byte[],Long>(new ByteUtils.BytesComparator());
	
	public MemorySpectrumBuilder(int k) {
		super(k);
	}
	 
	protected MemorySpectrumBuilder() {
		super();
	}
	
	@Override
	public void add(byte[] seq) {	
		if (map.containsKey(seq))
			map.put(seq, map.get(seq)+1);
		else {
			map.put(seq,1l);
			nkmers++;
		}
	}

	public void save(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		SpectrumIO.writeHeader(dos,new SpectrumIO.SpectrumHeader(k,nkmers));
		for(Entry<byte[],Long> pair: map.entrySet()) {
			SpectrumIO.writeKmer(dos, new Kmer(pair.getKey(),pair.getValue()));
			if (pc!=null) pc.incr(1);
		}
		out.close();
	};
	
	public Spectrum getSpectrum() {		
		MemorySpectrum spc = new MemorySpectrum(k,nkmers);
		spc.setMap(map);
		return spc;
	}
	
	@Override
	public void finish() {
		finished = true;
		System.gc();
		log.info(String.format("Memory Spectrum Used memory = %s",TextUtils.formatBytes(RuntimeUtils.usedMemory())));
	}
}
