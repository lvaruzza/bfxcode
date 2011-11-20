package bfx.io.impl;

import org.junit.Test;

import bfx.impl.FastQRepr;
import bfx.impl.FastaQualRepr;
import static org.junit.Assert.*;

public class TestFastQRepr {

	private FastQRepr fq = new FastQRepr();
	private FastaQualRepr fasta = new FastaQualRepr();
	
	
	@Test
	public void test1() {
		byte[] q = fq.textToQual("711108+//'*-8888'+*++++&+5555/5/555/++++&+++-/---(-2222255555222*2-)-/000)))$)0-///(//)+++");
		assertEquals("22 16 16 16 15 23 10 14 14 6 9 12 23 23 23 23 6 10 9 10 10 10 10 5 10 20 20 20 20 14 20 14 20 20 20 14 10 10 10 10 5 10 10 10 12 14 12 12 12 7 12 17 17 17 17 17 20 20 20 20 20 17 17 17 9 17 12 8 12 14 15 15 15 8 8 8 3 8 15 12 14 14 14 7 14 14 8 10 10 10",fasta.qualToTextString(q));
				
	}
	
}
