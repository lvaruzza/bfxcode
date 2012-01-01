package bfx.seqenc;

import bfx.Sequence;
import bfx.impl.SequenceConstQualImpl;
import bfx.utils.AliasMethodGenerator;

public class DNASeqGenerator {
	private AliasMethodGenerator gen;
	
	public DNASeqGenerator(Double... probabilities) {
		gen = new AliasMethodGenerator(probabilities);
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
