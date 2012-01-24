package bfx.spectrum;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Test;

import bfx.io.SequenceSource;
import bfx.utils.TextUtils;

public class TestMapAndMegeSpectrumBuilder {
	private static Logger log = Logger.getLogger(TestMapAndMegeSpectrumBuilder.class);
	
	@Test
	public void test0() throws IOException {
		MapAndMegeSpectrumBuilder sb = new MapAndMegeSpectrumBuilder(3,10);
		sb.add(SequenceSource.fromFile("fasta","data/test/spectrumU3.fasta"));	
		sb.finish();
		
		log.debug(TextUtils.doubleLine());
		sb.mergeLevel(0, sb.getNparts());
		log.debug(TextUtils.doubleLine());
		sb.mergeLevel(1, sb.getNparts()/2);
		log.debug(TextUtils.doubleLine());
		sb.mergeLevel(2, sb.getNparts()/4);
		log.debug(TextUtils.doubleLine());
		sb.mergeLevel(3, sb.getNparts()/8);

		MemorySpectrumBuilder msb = new MemorySpectrumBuilder(3);
		msb.add(SequenceSource.fromFile("fasta","data/test/spectrumU3.fasta"));	
		msb.finish();
		msb.save("fromMemory.spec");
		
		DiskSpectrum dsk1 = new DiskSpectrum(sb.getPartName(4,0));
		DiskSpectrum dsk2 = new DiskSpectrum("fromMemory.spec");

		dsk1.dump(System.out);
		System.out.println(TextUtils.doubleLine());
		dsk2.dump(System.out);
		
		
	}
}
