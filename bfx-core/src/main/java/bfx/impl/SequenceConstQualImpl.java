package bfx.impl;

import java.util.Arrays;

import bfx.Sequence;

public class SequenceConstQualImpl extends Sequence {
	private byte[] seq;
	private byte qual;

	public byte[] getSeq() {
		return seq;
	}
	public byte[] getQual() {
		byte[] q = new byte[seq.length];
		Arrays.fill(q, qual);
		return q;
	}

	public SequenceConstQualImpl(String header,byte[] seq, byte qual) {
		super(header);
		this.seq = seq;
		this.qual = qual;
	}

	public SequenceConstQualImpl(String id,String comments,byte[] seq, byte qual) {
		super(id,comments);
		this.seq = seq;
		this.qual = qual;
	}
	
	public SequenceConstQualImpl(String header,String seq, byte qual) {
		this(header,seq.getBytes(),qual);
	}
	
	@Override
	public Sequence changeSeq(byte[] newseq) {
		return new SequenceConstQualImpl(this.getId(),this.getComments(),newseq,qual);
	}
	
	@Override
	public String toString() {
		return "Sequence [Id=" + getId() + ", seq=" +
				getSeqAsString() + ", qual=" + qual + "]";
	}
	
	
}
