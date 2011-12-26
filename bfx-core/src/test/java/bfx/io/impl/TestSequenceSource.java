package bfx.io.impl;

import org.junit.Test;

import bfx.Sequence;
import bfx.io.SequenceSource;

public class TestSequenceSource {
	
	@Test
	public void testConstructor() {
		SequenceSource src1 = new FileSequenceSource("data/test/ncbi_small.fasta");
		SequenceSource src2 = new FileSequenceSource("fasta","data/test/ncbi_small.fasta",(String)null);
		SequenceSource src3 = new FileSequenceSource(null,"data/test/ncbi_small.fasta",(String)null);

		/*
		for(Sequence seq: src) {			
			System.out.println(seq);
		}*/
	}
	
	@Test
	public void testRead() {
		SequenceSource src = new FileSequenceSource("fasta","data/test/sample.csfasta",
							 							    "data/test/sample.qual");
		
		for(Sequence seq: src) {
			System.out.println(seq.changeSeq(seq.getSeq()));
		}
	}
}
