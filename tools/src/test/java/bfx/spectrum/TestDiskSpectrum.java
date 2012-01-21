package bfx.spectrum;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import bfx.utils.TextUtils;

public class TestDiskSpectrum {
	
	private static String expectedDump=
			"GTCA\t1\n"+
			"CGTC\t1\n"+
			"ACGT\t2\n";
	
	private static String expectedDumpDoubled =
			"GTCA\t2\n"+
			"CGTC\t2\n"+
			"ACGT\t4\n";
	

	private static String expectedDumpDiff =
			"GTCA\t3\n"+
			"CGTC\t2\n"+
			"ACGT\t5\n"+
			"ACGA\t1\n";
	
	@Before
	public void setup() throws IOException {
		MemorySpectrumBuilder spc = new MemorySpectrumBuilder(4);
		spc.add("ACGT".getBytes());
		spc.add("ACGT".getBytes());
		spc.add("CGTC".getBytes());
		spc.add("GTCA".getBytes());
		spc.save("test4.spec");
		
		MemorySpectrumBuilder spc2 = new MemorySpectrumBuilder(4);
		spc.add("ACGT".getBytes());
		spc.add("ACGA".getBytes());
		spc.add("GTCA".getBytes());
		spc.save("test4b.spec");
		
	}
	
	@Test
	public void testIterator() throws IOException {
		DiskSpectrum dspc = new DiskSpectrum("test4.spec");
		assertEquals(4,dspc.getK());
		assertEquals(3,dspc.numberOfKmers());
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();		
		dspc.dump(new PrintStream(out));
		assertEquals(expectedDump,out.toString());
	}
	
	@Test
	public void testMergeEquals() throws IOException {
		DiskSpectrum a = new DiskSpectrum("test4.spec");
		DiskSpectrum b = new DiskSpectrum("test4.spec");
		
		File out = new File("testMerge.spec");
		DiskSpectrum.merge(out, a, b);
		DiskSpectrum merge = new DiskSpectrum("testMerge.spec");
		
		ByteArrayOutputStream outDump = new ByteArrayOutputStream();		
		merge.dump(new PrintStream(outDump));
		assertEquals(expectedDumpDoubled,outDump.toString());		
	}
	
	@Test
	public void testMergeDifferent() throws IOException {
		DiskSpectrum a = new DiskSpectrum("test4.spec");
		DiskSpectrum b = new DiskSpectrum("test4b.spec");
		
		File out = new File("testMerge.spec");
		DiskSpectrum.merge(out, a, b);
		DiskSpectrum merge = new DiskSpectrum("testMerge.spec");

		ByteArrayOutputStream outDump = new ByteArrayOutputStream();		
		merge.dump(new PrintStream(outDump));
		assertEquals(expectedDumpDiff,outDump.toString());		
	}	
	
}
