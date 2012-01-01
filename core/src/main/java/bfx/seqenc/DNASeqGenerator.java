package bfx.seqenc;

import bfx.Sequence;
import bfx.impl.SequenceConstQualImpl;
import bfx.utils.AliasMethodGenerator;

public class DNASeqGenerator {
	private AliasMethodGenerator gen;
	
	private static Double[] defaultProbs = new Double[] {0.25-0.25/1000,
		 0.25-0.25/1000,
		 0.25-0.25/1000,
		 0.25-0.25/1000,
		 1.0/1000
	};
	
	public DNASeqGenerator(Double... probabilities) {
		gen = new AliasMethodGenerator(probabilities);
	}

	public DNASeqGenerator() {
		gen = new AliasMethodGenerator(defaultProbs);
	}
	
	public byte[] genBytesSeq(int len) {
		byte[] r = new byte[len];
		for(int i =0;i<len;i++)
			r[i] = Color.bases[gen.next()];
		return r;
	}
	public Sequence genSeq(String header,int len,byte qual) {
		return new SequenceConstQualImpl(header,genBytesSeq(len),qual);
	}
}
