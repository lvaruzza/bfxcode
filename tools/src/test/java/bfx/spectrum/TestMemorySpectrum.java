package bfx.spectrum;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

public class TestMemorySpectrum {
	private static String expectedDump=
			"ACGT\t2\n"+
			"CGTC\t1\n"+
			"GTCA\t1\n";

	public MemorySpectrumBuilder createSpectrum() throws IOException {
		MemorySpectrumBuilder spc = new MemorySpectrumBuilder(4);
		spc.add("ACGT".getBytes());
		spc.add("ACGT".getBytes());
		spc.add("CGTC".getBytes());
		spc.add("GTCA".getBytes());
		return spc;
	}
	
	@Test
	public void testDump() throws IOException {
		MemorySpectrumBuilder spcb = createSpectrum();
		Spectrum spc = spcb.getSpectrum();
		assertEquals(4,spc.getK());
		assertEquals(3,spc.numberOfKmers());

		ByteArrayOutputStream out = new ByteArrayOutputStream();		
		spc.dump(new PrintStream(out));
		assertEquals(expectedDump,out.toString());
	}
	
	@Test
	public void testSaveLoad() throws IOException {
		// Save
		MemorySpectrumBuilder spc = createSpectrum();
		ByteArrayOutputStream out = new ByteArrayOutputStream();		
		spc.save(out);
		
		// Load
		byte[] data = out.toByteArray();
		out = null;
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		Spectrum spc2 = MemorySpectrum.load(in);
		
		// Verify
		ByteArrayOutputStream dump = new ByteArrayOutputStream();		
		spc2.dump(new PrintStream(dump));
		System.out.println(dump.toString());
		assertEquals(expectedDump,dump.toString());
	}	
}
