package bfx.seqenc;

import org.junit.Test;

public class TestColorSeqGenerator {

	@Test
	public void test0() {
		ColorSeqGenerator gen = new ColorSeqGenerator(0.25,0.25,0.25,0.249,0.001);
		
		for(int i = 0; i < 10; i++) {
			System.out.println(gen.genSeq(String.format("Sequence%d",i), (byte)'T',60, (byte)0));
		}
	}
}
