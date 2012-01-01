package bfx.seqenc;

import bfx.Sequence;
import bfx.impl.SequenceConstQualImpl;
import bfx.utils.AliasMethodGenerator;

public class ColorSeqGenerator {
	private AliasMethodGenerator gen;
	private static Double[] defaultProbs = new Double[] {0.25-0.25/1000,
														 0.25-0.25/1000,
														 0.25-0.25/1000,
														 0.25-0.25/1000,
														 1.0/1000
	};
	
	public ColorSeqGenerator(Double... probabilities) {
		gen = new AliasMethodGenerator(probabilities);
	}

	public ColorSeqGenerator() {
		gen = new AliasMethodGenerator(defaultProbs);
	}
	
	public byte[] genBytesSeq(int len) {
		byte[] r = new byte[len];
		for(int i =0;i<len;i++)
			r[i] = Color.colors[gen.next()];
		return r;
	}

	public byte[] genBytesSeqWithFirstBase(byte firstBase,int len) {
		byte[] r = new byte[len+1];
		r[0] = firstBase;
		for(int i = 1;i<=len;i++)
			r[i] = Color.colors[gen.next()];
		return r;
	}
	
	public Sequence genSeq(String header,int len,byte qual) {
		return new SequenceConstQualImpl(header,genBytesSeq(len),qual);
	}

	public Sequence genSeq(String header,byte firstBase,int len,byte qual) {
		return new SequenceConstQualImpl(header,genBytesSeqWithFirstBase(firstBase,len),qual);
	}
	
	// TODO variable quality generator
}
