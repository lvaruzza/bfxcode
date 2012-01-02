package bfx.seqenc;

import bfx.Sequence;
import bfx.impl.SequenceConstQual;
import bfx.utils.AliasMethodGenerator;

public class DNASeqGenerator {
	private AliasMethodGenerator gen;
	
	private static double[] defaultProbs = new double[] {0.25-0.25/1000,
		 0.25-0.25/1000,
		 0.25-0.25/1000,
		 0.25-0.25/1000,
		 1.0/1000
	};
	
	public DNASeqGenerator(double... probabilities) {
		if (probabilities.length != 5)
			throw new IllegalArgumentException(String.format("ColorGenerator needs exactly 5 probabilites values. Intead %d provieded",
													probabilities.length));
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
		return new SequenceConstQual(header,genBytesSeq(len),qual);
	}
}
