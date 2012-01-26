package bfx.spectrum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class MapAndMergeSpectrumBuilder extends SpectrumBuilder{
	private static Logger log = Logger.getLogger(MapAndMergeSpectrumBuilder.class);
	
	private MemorySpectrumBuilder spec;
	private int memoryLimit;
	private int numParts;
	private String basename;
	private File tempDir;
	
	public MapAndMergeSpectrumBuilder(int k,int memoryLimit,File tempDir) {
		super(k);
		
		this.memoryLimit = memoryLimit;
		this.spec = new MemorySpectrumBuilder(k);
		this.numParts = 0;
		this.basename = ".spectrum" + System.currentTimeMillis();
		this.tempDir = tempDir;
	}
	
	public String getPartName(int level,int x) {
		return tempDir.getAbsolutePath() + "/" + basename + "-" + level + "." + x + ".dat";
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

	public void merge(File a,File b,File out) throws IOException {
		DiskSpectrum sa = new DiskSpectrum(a);
		DiskSpectrum sb = new DiskSpectrum(b);
		SpectrumIO.merge(out, sa, sb);
		a.delete();
		b.delete();
	}
	
	public void mergeLevel(int level, int n) throws IOException {
		log.debug(String.format("n=%d",n));
		
		for(int i=0;i<n/2;i++) {
			File a=new File(getPartName(level,2*i));
			File b=new File(getPartName(level,2*i+1));
			File out=new File(getPartName(level+1,i));
			merge(a,b,out);
			
		}
		
		if (n%2==1) {
			File last=new File(getPartName(level, n-1));
			File nxlevel=new File(getPartName(level+1,n/2));
			log.debug(String.format("Even number of parts. Moving '%s' to '%s",last,nxlevel));
			last.renameTo(nxlevel);
		}
		
	}

	private int lastLevel=-1;
	
	public void mergeAll(int n) throws IOException  {
		int level=0;
		for(int i=n;i>=1;i=(int)(i/2.0+0.5),level++) {
			mergeLevel(level,i);
		}
		lastLevel = level;
	}

	private File lastFile() {
		return new File(getPartName(lastLevel,0));		
	}
	
	@Override
	public void save(OutputStream out) throws IOException {
		IOUtils.copyLarge(new FileInputStream(lastFile()), out);		
	}
	
	@Override
	public void save(String outName) throws IOException {
		File lastFile = lastFile();
		File out = new File(outName);
		if (out.exists())
			out.delete();
		lastFile.renameTo(out);
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
