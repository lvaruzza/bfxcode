package bfx.impl;

import org.apache.log4j.Logger;

import bfx.Sequence;

public class SequenceQualImpl extends Sequence {
	private static Logger log = Logger.getLogger(SequenceQualImpl.class);
	
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
	public SequenceQualImpl(String header,String seq,String repr) {
		this(header, seq.getBytes(), qualrepr.stringToQual(repr));
	}
}
