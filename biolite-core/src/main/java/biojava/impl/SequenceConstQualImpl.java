package biojava.impl;

import java.util.Arrays;

import biolite.Sequence;

public class SequenceConstQualImpl implements Sequence {
	private byte[] seq;
	private byte qual;
	private String id;
	
	public String getId() {
		return id;
	}
	public byte[] getSeq() {
		return seq;
	}
	public byte[] getQual() {
		byte[] q = new byte[seq.length];
		Arrays.fill(q, qual);
		return q;
	}
	
	public SequenceConstQualImpl(String id, byte[] seq, byte qual) {
		super();
		this.id = id;
		this.seq = seq;
		this.qual = qual;
	}
	
	public SequenceConstQualImpl(String id, String seq, byte qual) {
		this(id,seq.getBytes(),qual);
	}	
}
