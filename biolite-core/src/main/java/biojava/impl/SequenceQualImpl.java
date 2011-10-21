package biojava.impl;

import biolite.Sequence;

public class SequenceQualImpl implements Sequence {
	private byte[] seq;
	private byte[] qual;
	private String id;
	
	public String getId() {
		return id;
	}
	public byte[] getSeq() {
		return seq;
	}
	public byte[] getQual() {
		return qual;
	}
	
}
