package bfx.impl;

import java.util.Arrays;

import bfx.Sequence;

public class SequenceQualImpl extends Sequence {
	//private static Logger log = Logger.getLogger(SequenceQualImpl.class);
	
	private static FastaQualRepr qualrepr = new FastaQualRepr();
	
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

	public SequenceQualImpl(String id,String comment,byte[] seq,byte[] qual) {
		super(id,comment);
		this.seq = seq;
		this.qual = qual;
		//System.out.println("New seq = " + this.toString());
	}	
	
	public SequenceQualImpl(String header,String seq,String repr) {
		this(header, seq.getBytes(), qualrepr.textToQual(repr));
	}

	@Override
	public Sequence changeSeq(byte[] newseq) {
		return new SequenceQualImpl(this.getId(),this.getComments(),newseq,getQual());
	}
}
