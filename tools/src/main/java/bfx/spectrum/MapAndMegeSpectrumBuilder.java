package bfx.spectrum;

import java.io.IOException;
import java.io.OutputStream;

public class MapAndMegeSpectrumBuilder extends SpectrumBuilder{
	private MemorySpectrumBuilder spec;
	private int memoryLimit;
	private int numParts;
	private String basename;
	
	public MapAndMegeSpectrumBuilder(int k,int memoryLimit) {
		super(k);
		
		this.memoryLimit = memoryLimit;
		this.spec = new MemorySpectrumBuilder(k);
		this.numParts = 0;
		this.basename = ".spectrum" + System.currentTimeMillis();
	}
	
	private String getPartName(int x) {
		return basename + "-" + x + ".dat";
	}
	
	@Override
	protected void add1(byte[] seq) throws IOException {
		spec.add1(seq);
		nkmers = spec.nkmers;
		
		if (spec.nkmers == memoryLimit) {
			spec.save(getPartName(numParts));
			this.spec = new MemorySpectrumBuilder(k);
			System.gc();
			numParts++;
		}
	}

	public int merge(int a,int b) {
		return 0;
	}
	
	@Override
	public void save(OutputStream out) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}

}
