package bfx.io.impl;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import bfx.Sequence;
import bfx.impl.SequenceConstQual;
import bfx.io.SequenceFormats;
import bfx.io.SequenceWriter;
import bfx.utils.TextUtils;

public class TestFastaWriter {
	@Test
	public void testGetWriter() {
		SequenceWriter sw1 = SequenceFormats.getWriterForFile("test.fasta", "fasta");
		assertEquals("bfx.io.impl.FastaSequenceWriter",sw1.getClass().getName());

		SequenceWriter sw2 = SequenceFormats.getWriterForFile("test.fasta", null);
		assertEquals("bfx.io.impl.FastaSequenceWriter",sw2.getClass().getName());
	}
	
	@Test
	public void testWriter() throws IOException {
		SequenceWriter sw = new FastaSequenceWriter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String name  = "test";
		Sequence seq = new SequenceConstQual(name,TextUtils.times("ACGT",80),(byte)0);
		sw.write(out, seq);
		String r = out.toString();
		assertEquals(1+4*80+4+name.length()+1,r.length());
		String[] rs = r.split("\n");
		assertEquals(5,rs.length);
		for(int i=1;i<rs.length;i++)
			assertEquals(TextUtils.times("ACGT",20),rs[i]);
	}
	
	@Test
	public void testQualWriter() throws IOException {
		SequenceWriter sw = new FastaSequenceWriter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream qual = new ByteArrayOutputStream();
		Sequence seq = new SequenceConstQual("test",TextUtils.times("ACGT",80),(byte)42);
		sw.write(out, qual,seq);
		String r = qual.toString();
		//System.out.println(r.replace("\n", "|\n"));
		String[] rs = r.split("\n");
		assertEquals(5,rs.length);
		String[] vs = rs[2].split(" ");
		//System.out.println(Arrays.toString(vs));
		assertEquals(80,vs.length);
		//System.out.println(Arrays.toString(vs));
	}
}
