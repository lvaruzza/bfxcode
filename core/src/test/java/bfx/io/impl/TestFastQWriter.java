package bfx.io.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import bfx.Sequence;
import bfx.impl.SequenceConstQual;
import bfx.io.SequenceFormats;
import bfx.io.SequenceWriter;
import bfx.utils.TextUtils;
import static org.junit.Assert.*;

public class TestFastQWriter {
	@Test
	public void testGetWriter() {
		SequenceWriter sw1 = SequenceFormats.getWriterForFile("test.fastq", "fasta");
		assertEquals("bfx.io.impl.FastQSequenceWriter",sw1.getClass().getName());

		SequenceWriter sw2 = SequenceFormats.getWriterForFile("test.fastq", null);
		assertEquals("bfx.io.impl.FastQSequenceWriter",sw2.getClass().getName());
	}
	
	@Test
	public void testWriter() throws IOException {
		SequenceWriter sw = new FastQSequenceWriter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String name  = "s1 sequence1";
		Sequence seq = new SequenceConstQual(name,TextUtils.times("ACGT",80),(byte)0);
		sw.write(out, seq);
		String r = out.toString();
		System.out.println(r);
		assertEquals(2*4*80+1+name.length()+4+1,r.length());
		/*String[] rs = r.split("\n");
		assertEquals(5,rs.length);
		for(int i=1;i<rs.length;i++)
			assertEquals(TextUtils.times("ACGT",20),rs[i]);*/
	}
}
