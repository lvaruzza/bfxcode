package bfx.io.impl;

import org.junit.Test;

import bfx.Sequence;
import bfx.io.SequenceSource;

public class TestSequenceSource {
	
	@Test
	public void testConstructor() {
		SequenceSource src1 = new FileSequenceSource("data/test/ncbi_small.fasta");
		SequenceSource src2 = new FileSequenceSource("data/test/ncbi_small.fasta",(String)null);

		/*
		for(Sequence seq: src) {			
			System.out.println(seq);
		}*/
	}
}
