package bfx.spectrum;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.TreeMap;

import bfx.utils.ByteUtils;

public class MemorySpectrumBuilder extends SpectrumBuilder  {
	//private static Logger log = Logger.getLogger(MemorySpectrumBuilder.class);
	
	private TreeMap<byte[],Long> map = new TreeMap<byte[],Long>(new ByteUtils.BytesComparator());
	
	public MemorySpectrumBuilder(int k) {
		super(k);
	}
	 
	protected MemorySpectrumBuilder() {
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

	public void save(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		writeHeader(dos);
		for(Entry<byte[],Long> pair: map.entrySet()) {
			dos.write(pair.getKey());
			dos.writeLong(pair.getValue());
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
	}
}
