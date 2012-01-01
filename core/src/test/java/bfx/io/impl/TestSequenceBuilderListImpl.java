package bfx.io.impl;

import org.junit.Test;

import bfx.Sequence;
import bfx.SequenceBuilder;
import bfx.impl.SequenceBuilderListImpl;
import bfx.impl.SequenceQualImpl;
import static org.junit.Assert.*;

public class TestSequenceBuilderListImpl {

	@Test
	public void testSameSeq10x() {
		SequenceBuilder sb = new SequenceBuilderListImpl();
		byte[] text = "ACGT".getBytes();
		Sequence s = new SequenceQualImpl("1",new String(text),"1 2 3 4");
		for(int i = 0;i<10;i++) {
			sb.append(s);
		}
		Sequence r = sb.getWithQual("result");
		System.out.println(r);
		assertEquals("result",r.getId());
		assertEquals(40,r.length());
		byte[] qual = r.getQual();
		byte[] seq = r.getSeq();
		
		for(int i=0;i<r.length();i++) {
			assertEquals((byte)((i % 4)+1),qual[i]);
			assertEquals(text[i%4],seq[i]);
		}
	}
}
