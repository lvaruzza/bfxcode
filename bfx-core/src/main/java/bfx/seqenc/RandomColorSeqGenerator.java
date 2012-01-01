package bfx.seqenc;

import bfx.utils.AliasMethodGenerator;

public class RandomColorSeqGenerator {
	private AliasMethodGenerator gen;
	
	public RandomColorSeqGenerator(Double... probabilities) {
		gen = new AliasMethodGenerator(probabilities);
	}
	
	public byte[] genBytesSeq(int len) {
		byte[] r = new byte[len];
		for(int i =0;i<len;i++)
			r[i] = Color.colors[gen.next()];
		return r;
	}
}
