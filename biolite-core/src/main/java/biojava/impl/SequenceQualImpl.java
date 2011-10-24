package biojava.impl;

import biolite.Sequence;

public class SequenceQualImpl extends Sequence {
	private static QualRepr qualrepr = new QualRepr();
	
	private byte[] seq;
	private byte[] qual;

	public byte[] getSeq() {
		return seq;
	}
	public byte[] getQual() {
		return qual;
	}

	public SequenceQualImpl(String header,byte[] seq,byte[] qual) {
		super(header);
		this.seq = seq;
		this.qual = qual;
	}	
	public SequenceQualImpl(String header,String seq,String qual) {
		this(header, seq.getBytes(), qualrepr.stringToQual(qual));
	}
}
