package bfx.io.impl;

import org.junit.Test;

import bfx.Sequence;
import bfx.io.SequenceSource;
import static org.junit.Assert.*;

public class TestSequenceSource {
	
	@Test
	public void testConstructor() {
		SequenceSource src1 = new FileSequenceSource("data/test/ncbi_small.fasta");
		SequenceSource src2 = new FileSequenceSource("fasta","data/test/ncbi_small.fasta",(String)null);
		SequenceSource src3 = new FileSequenceSource(null,"data/test/ncbi_small.fasta",(String)null);

		assertNotNull(src1);
		assertNotNull(src2);
		assertNotNull(src3);
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
