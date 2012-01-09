package bfx.io.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bfx.io.SequenceSource;
import bfx.utils.TextUtils;

public class TestKmerIterable {

	@Test
	public void test0() {
		SequenceSource src = SequenceSource.fromString("fasta",
				">1\n" +
				"ACGACGACGAN\n" +
				">2\n" +
				"ACGTACGTACGT\n");
		
		Iterable<byte[]> kmers = src.kmers(3);
		
		int i=0;
		byte[] prev = null;
		
		for(byte[] kmer: kmers) {
			System.out.print(TextUtils.times(' ', i++));
			System.out.println(new String(kmer));
			if (prev != null) {
				assertEquals(prev[1],kmer[0]);
				assertEquals(prev[2],kmer[1]);
			}
			prev = kmer;
			if (kmer[2] == 'N') { 
				i=0;
				prev = null;
			}
		}
	}
}
