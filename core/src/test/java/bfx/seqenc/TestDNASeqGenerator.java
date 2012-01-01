package bfx.seqenc;

import org.junit.Test;

public class TestDNASeqGenerator {

	@Test
	public void test0() {
		DNASeqGenerator gen = new DNASeqGenerator(0.25,0.25,0.25,0.249,0.001);
		
		for(int i = 0; i < 10; i++) {
			System.out.println(gen.genSeq(String.format("Sequence%d",i),60, (byte)0));
		}
	}
}
