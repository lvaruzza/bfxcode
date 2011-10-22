package biojava.impl;

import biolite.Sequence;

public class SequenceQualImpl extends Sequence {
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
	}	
}
