package bfx.spectrum;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

public class TestSpectrumMerge {
	
	private static String expectedDump=
			"ACGT\t2\n"+
			"CGTC\t1\n"+
			"GTCA\t1\n";
	
	private static String expectedDumpDoubled =
			"ACGT\t4\n"+
			"CGTC\t2\n"+
			"GTCA\t2\n";
	

	private static String expectedDumpDiff =
			"ACGA\t1\n"+
			"ACGT\t3\n"+
			"CGTC\t1\n"+
			"GTCA\t2\n";
	
	private static String expectedNoOverlap =
			"ACAT	2\n" +
			"ACGT	2\n" +
			"CGTC	1\n" +
			"CTTC	1\n" +
			"GTCA	1\n" +
			"GTCC	1\n" +
			"TTTT	1\n";
	
	@Before
	public void setup() throws IOException {
		MemorySpectrumBuilder spc = new MemorySpectrumBuilder(4);
		spc.add("GTCA".getBytes());
		spc.add("ACGT".getBytes());
		spc.add("ACGT".getBytes());
		spc.add("CGTC".getBytes());
		spc.save("test4.spec");
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
	public void testFiles() throws IOException {
		DiskSpectrum a = new DiskSpectrum("test4.spec");
		assertEquals(4,a.getK());
		assertEquals(3,a.numberOfKmers());
	}
	
	@Test
	public void testMergeEquals() throws IOException {
		DiskSpectrum a = new DiskSpectrum("test4.spec");
		DiskSpectrum b = new DiskSpectrum("test4.spec");

		a.dump(System.out);
		b.dump(System.out);
		
		File out = new File("testMerge.spec");
		SpectrumIO.merge(out, a, b);
		DiskSpectrum merge = new DiskSpectrum(out);
		
		merge.dump(System.out);
		
		ByteArrayOutputStream outDump = new ByteArrayOutputStream();		
		merge.dump(new PrintStream(outDump));
		assertEquals(expectedDumpDoubled,outDump.toString());		
	}
	
	@Test
	public void testMergeDifferent() throws IOException {
		// Create test4b
		MemorySpectrumBuilder spc = new MemorySpectrumBuilder(4);
		spc.add("GTCA".getBytes());
		spc.add("ACGT".getBytes());
		spc.add("ACGA".getBytes());
		spc.save("test4b.spec");
		

		DiskSpectrum a = new DiskSpectrum("test4.spec");
		DiskSpectrum b = new DiskSpectrum("test4b.spec");
		
		File out = new File("testMerge.spec");
		SpectrumIO.merge(out, a, b);
		DiskSpectrum merge = new DiskSpectrum(out);

		merge.dump(System.out);

		ByteArrayOutputStream outDump = new ByteArrayOutputStream();		
		merge.dump(new PrintStream(outDump));
		assertEquals(expectedDumpDiff,outDump.toString());		
	}	

	@Test
	public void testMergeNoOverlap() throws IOException {
		// Create test4c
		MemorySpectrumBuilder spc = new MemorySpectrumBuilder(4);
		spc.add("GTCC".getBytes());
		spc.add("ACAT".getBytes());
		spc.add("ACAT".getBytes());
		spc.add("CTTC".getBytes());
		spc.add("TTTT".getBytes());
		spc.save("test4c.spec");

		DiskSpectrum a = new DiskSpectrum("test4.spec");
		DiskSpectrum b = new DiskSpectrum("test4c.spec");
		
		File out = new File("testMergeNoOver.spec");
		SpectrumIO.merge(out, a, b);
		DiskSpectrum merge = new DiskSpectrum(out);

		merge.dump(System.out);

		ByteArrayOutputStream outDump = new ByteArrayOutputStream();		
		merge.dump(new PrintStream(outDump));
		assertEquals(expectedNoOverlap,outDump.toString());		
	}	
	
}
