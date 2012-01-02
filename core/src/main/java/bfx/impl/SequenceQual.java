package bfx.impl;

import bfx.Sequence;

public class SequenceQual extends Sequence {
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

	public SequenceQual(String header,byte[] seq,byte[] qual) {
		super(header);
		this.seq = seq;
		this.qual = qual;
	}	

	public SequenceQual(String id,String comment,byte[] seq,byte[] qual) {
		super(id,comment);
		this.seq = seq;
		this.qual = qual;
		//System.out.println("New seq = " + this.toString());
	}	
	
	public SequenceQual(String header,String seq,String repr) {
		this(header, seq.getBytes(), qualrepr.textToQual(repr));
	}

	@Override
	public Sequence changeSeq(byte[] newseq) {
		return new SequenceQual(this.getId(),this.getComments(),newseq,getQual());
	}
}
