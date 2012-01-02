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

public class TestFastaWriter {
	@Test
	public void testGetWriter() {
		SequenceWriter sw1 = SequenceFormats.getWriter("test.fasta", "fasta");
		assertEquals("bfx.io.impl.FastaSequenceWriter",sw1.getClass().getName());

		SequenceWriter sw2 = SequenceFormats.getWriter("test.fasta", null);
		assertEquals("bfx.io.impl.FastaSequenceWriter",sw2.getClass().getName());
	}
	
	@Test
	public void testWriter() throws IOException {
		SequenceWriter sw = new FastaSequenceWriter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String name  = out.toString();
		Sequence seq = new SequenceConstQual(name,TextUtils.times("ACGT",80),(byte)0);
		sw.write(out, seq);
		String r = out.toString();
		assertEquals(1+4*80+4+name.length()+1,r.length());
		String[] rs = r.split("\n");
		assertEquals(5,rs.length);
		for(int i=1;i<rs.length;i++)
			assertEquals(TextUtils.times("ACGT",20),rs[i]);
	}
}
