package bfx.spectrum;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class MapAndMegeSpectrumBuilder extends SpectrumBuilder{
	private static Logger log = Logger.getLogger(MapAndMegeSpectrumBuilder.class);
	
	private MemorySpectrumBuilder spec;
	private int memoryLimit;
	private int numParts;
	private String basename;
	
	public MapAndMegeSpectrumBuilder(int k,int memoryLimit) {
		super(k);
		
		this.memoryLimit = memoryLimit;
		this.spec = new MemorySpectrumBuilder(k);
		this.numParts = 0;
		this.basename = ".spectrum"; // + System.currentTimeMillis();
	}
	
	public String getPartName(int level,int x) {
		return basename + "-" + level + "." + x + ".dat";
	}
	
	private void savePart() throws IOException {
		spec.save(getPartName(0,numParts));
		System.gc();
		numParts++;		
	}
	
	@Override
	public void add(byte[] seq) throws IOException {
		spec.add(seq);
		nkmers = spec.nkmers;
		
		if (spec.nkmers == memoryLimit) {
			savePart();
			this.spec = new MemorySpectrumBuilder(k);
		}
	}

	public void merge(int level,int a,int b,int outNum) throws IOException {
		log.debug(String.format("Merging %d.%d and %d.%d into %d.%d",level,a,level,b,level+1,outNum));
		DiskSpectrum sa = new DiskSpectrum(getPartName(level,a));
		DiskSpectrum sb = new DiskSpectrum(getPartName(level,b));
		String outName = getPartName(level+1,outNum);
		SpectrumIO.merge(new File(outName), sa, sb);
	}
	
	public void mergeLevel(int level, int n) throws IOException {
		log.debug(String.format("n=%d",n));
		
		for(int i=0;i<n/2;i++) {
			merge(level,2*i,2*i+1,i);
		}
		if (n%2==1) {
			File last=new File(getPartName(level, n-1));
			File nxlevel=new File(getPartName(level+1,n/2));
			log.debug(String.format("Even number of parts. Moving '%s' to '%s",last,nxlevel));
			last.renameTo(nxlevel);
		}
		
	}

	public void mergeAll(int n) throws IOException  {
		int level=0;
		for(int i=n;i>=1;i=(int)(i/2.0+0.5),level++) {
			mergeLevel(level,i);
		}
	}
	
	@Override
	public void save(OutputStream out) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() throws IOException {
		// Save the last part
		log.info(String.format("Kmers in the last part %d",spec.nkmers));
		if (spec.nkmers>0) savePart();
		log.info("Finished building spectrum");
	}

	public int getNparts() {
		return this.numParts;
	}

}
