package bfx.spectrum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

import bfx.utils.TextUtils;
import static org.junit.Assert.*;

public class TestMemorySpectrum {
	private static String expectedDump=
			"GTCA\t1\n"+
			"CGTC\t1\n"+
			"ACGT\t2\n";
	
	public MemorySpectrum createSpectrum() {
		MemorySpectrum spc = new MemorySpectrum(4);
		spc.add("ACGT".getBytes());
		spc.add("ACGT".getBytes());
		spc.add("CGTC".getBytes());
		spc.add("GTCA".getBytes());
		return spc;
	}
	
	@Test
	public void testDump() {
		MemorySpectrum spc = createSpectrum();
		ByteArrayOutputStream out = new ByteArrayOutputStream();		
		spc.dump(new PrintStream(out));
		assertEquals(expectedDump,out.toString());
	}
	
	@Test
	public void testSaveLoad() throws IOException {
		// Save
		MemorySpectrum spc = createSpectrum();
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
