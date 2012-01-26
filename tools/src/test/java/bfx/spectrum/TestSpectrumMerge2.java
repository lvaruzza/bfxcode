package bfx.spectrum;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class TestSpectrumMerge2 {
	
	@Test
	public void testMerge1() throws IOException {
		DiskSpectrum s0 = new DiskSpectrum("data/test/spectrum-0.0.dat");
		DiskSpectrum s1 = new DiskSpectrum("data/test/spectrum-0.1.dat");
		SpectrumIO.merge(new File("testMerge.dat"), s0, s1);
		DiskSpectrum sm = new DiskSpectrum("testMerge.dat");
		sm.dump(System.out);
	}
	
}
