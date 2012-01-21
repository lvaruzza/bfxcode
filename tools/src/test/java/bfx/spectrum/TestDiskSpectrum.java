package bfx.spectrum;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class TestDiskSpectrum {

	@Before
	public void setup() throws IOException {
		MemorySpectrumBuilder spc = new MemorySpectrumBuilder(4);
		spc.add("ACGT".getBytes());
		spc.add("ACGT".getBytes());
		spc.add("CGTC".getBytes());
		spc.add("GTCA".getBytes());
		spc.save("test4.spec");
	}
	
	@Test
	public void testIterator() throws IOException {
		DiskSpectrum dspc = new DiskSpectrum("test4.spec");
		for(Kmer kmer: dspc) {
			System.out.println(kmer);
		}
	}
}
