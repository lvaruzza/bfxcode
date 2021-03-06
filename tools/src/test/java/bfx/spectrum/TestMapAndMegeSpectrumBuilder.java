package bfx.spectrum;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.io.SequenceSource;

public class TestMapAndMegeSpectrumBuilder {
	private static Logger log = LoggerFactory.getLogger(TestMapAndMegeSpectrumBuilder.class);
	
	/*@Test
	public void test0() throws IOException {
		MapAndMergeSpectrumBuilder sb = new MapAndMergeSpectrumBuilder(3,10,new File("."));
		sb.add(SequenceSource.fromFile("fasta","data/test/spectrumU3.fasta"));	
		//sb.finish();
		int n = sb.getNparts();
		log.debug(TextUtils.doubleLine());
		
		sb.mergeLevel(0, n);
		log.debug(TextUtils.doubleLine());		
		n = (int)(n/2.0+0.5);
		sb.mergeLevel(1, n);
		log.debug(TextUtils.doubleLine());
		n = (int)(n/2.0+0.5);
		sb.mergeLevel(2, n);
		log.debug(TextUtils.doubleLine());
		n = (int)(n/2.0+0.5);
		sb.mergeLevel(3, n);

		log.debug(TextUtils.doubleLine());
		n = (int)(n/2.0+0.5);
		sb.mergeLevel(4, n);
		
		MemorySpectrumBuilder msb = new MemorySpectrumBuilder(3);
		msb.add(SequenceSource.fromFile("fasta","data/test/spectrumU3.fasta"));	
		msb.finish();
		msb.save("fromMemory.spec");
		
		DiskSpectrum dsk1 = new DiskSpectrum(sb.getPartName(5,0));
		DiskSpectrum dsk2 = new DiskSpectrum("fromMemory.spec");

		//dsk1.dump(System.out);
		//System.out.println(TextUtils.doubleLine());
		//dsk2.dump(System.out);
		
		assertTrue(dsk1.equals(dsk2));
		
	}*/
	
	private void testFile(String filename,int k,int mem) throws IOException {
		MapAndMergeSpectrumBuilder sb = new MapAndMergeSpectrumBuilder(3,10,new File("."));
		sb.add(SequenceSource.fromFile("data/test/spectrumU3.fasta"));	
		sb.finish();
		sb.save("mm.spec");
		
		MemorySpectrumBuilder msb = new MemorySpectrumBuilder(3);
		msb.add(SequenceSource.fromFile("data/test/spectrumU3.fasta"));	
		msb.finish();
		msb.save("fromMemory.spec");
		
		DiskSpectrum dsk1 = new DiskSpectrum("mm.spec");
		DiskSpectrum dsk2 = new DiskSpectrum("fromMemory.spec");
		
		assertTrue(dsk1.equals(dsk2));				
	}
	
	@Test
	public void test1() throws IOException {
		testFile("data/test/spectrumU3.fasta",3,10);
	}

	
	@Test
	public void test2() throws IOException {
		testFile("../core/data/test/sample.csfasta",17,1000*1000);
	}	
	
	@Test
	public void test3() throws IOException {
		testFile("../core/data/test/ion.fastq",12,100*1000);
	}		
}
