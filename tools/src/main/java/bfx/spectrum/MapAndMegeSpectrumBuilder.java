package bfx.spectrum;

import java.io.File;
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
	
	private String getPartName(int level,int x) {
		return basename + "-" + level + "." + x + ".dat";
	}
	
	@Override
	public void add(byte[] seq) throws IOException {
		spec.add(seq);
		nkmers = spec.nkmers;
		
		if (spec.nkmers == memoryLimit) {
			spec.save(getPartName(0,numParts));
			this.spec = new MemorySpectrumBuilder(k);
			System.gc();
			numParts++;
		}
	}

	public void merge(int level,int a,int b,int outNum) throws IOException {
		DiskSpectrum sa = new DiskSpectrum(getPartName(level,a));
		DiskSpectrum sb = new DiskSpectrum(getPartName(level,b));
		String outName = getPartName(level+1,outNum);
		SpectrumIO.merge(new File(outName), sa, sb);
	}
	
	public void mergeLevel(int level, int n) throws IOException {
		for(int i=0;i<n;i+=2) {
			merge(level,i,i+1,i/2);
		}
	}

	public void mergeAll(int n) throws IOException  {
		int level=0;
		for(int i=n;i>=1;i=i/2,level++) {
			mergeLevel(level,i);
		}
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
