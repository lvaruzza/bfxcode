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
		if (pm!=null) pm.incr(1);
	}

	public void save(OutputStream out) throws IOException {
		if(pmf!=null) pm = pmf.get();
		if (pm!=null) pm.start("Saving Spectrum");
		DataOutputStream dos = new DataOutputStream(out);
		SpectrumIO.writeHeader(dos,new SpectrumIO.SpectrumHeader(k,nkmers));
		for(Entry<byte[],Long> pair: map.entrySet()) {
			SpectrumIO.writeKmer(dos, new Kmer(pair.getKey(),pair.getValue()));
			if (pm!=null) pm.incr(1);
		}
		out.close();
		if (pm!=null) pm.finish();
	};
	
	public Spectrum getSpectrum() {		
		MemorySpectrum spc = new MemorySpectrum(k,nkmers);
		spc.setMap(map);
		return spc;
	}
	
	@Override
	public void finish() {
		if (pm!=null) pm.finish();
		finished = true;
		System.gc();
		log.info(String.format("Memory Spectrum Used memory = %s",TextUtils.formatBytes(RuntimeUtils.usedMemory())));
	}
}
