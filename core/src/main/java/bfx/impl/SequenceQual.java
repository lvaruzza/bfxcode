package bfx.impl;

import java.util.Arrays;

import bfx.Sequence;
import bfx.utils.stat.OnlineMean;
import bfx.utils.stat.UnivariableStat;

public class SequenceQual extends Sequence {
	//private static Logger log = Logger.getLogger(SequenceQualImpl.class);
	
	private static FastaQualRepr qualrepr = new FastaQualRepr();
	
	private byte[] seq;
	private byte[] qual;

	/**
	 * Create new Sequence Object
	 * 
	 * @param header
	 * @param seq
	 * @param qual
	 */
	public SequenceQual(String header,byte[] seq,byte[] qual) {
		super(header);
		this.seq = seq;
		this.qual = qual;
	}	

	/**
	 * Create new Sequence Object
	 * 
	 * @param id Sequence ID
	 * @param comment Sequence Comments
	 * @param seq byte array Sequence Text
	 * @param qual byte array with Quality Values
	 */
	public SequenceQual(String id,String comment,byte[] seq,byte[] qual) {
		super(id,comment);
		this.seq = seq;
		this.qual = qual;
		//System.out.println("New seq = " + this.toString());
	}	
	
	/**
	 * Create new Sequence Object
	 * 
	 * @param header Sequence header
	 * @param seq String with Sequence Text
	 * @param repr FastaQual representation of quality values
	 */
	public SequenceQual(String header,String seq,String repr) {
		this(header, seq.getBytes(), qualrepr.textToQual(repr));
	}

	@Override
	public byte[] getSeq() {
		return seq;
	}
	
	@Override
	public byte[] getQual() {
		return qual;
	}	
	@Override
	public Sequence changeSeq(byte[] newseq) {
		return new SequenceQual(this.getId(),this.getComments(),newseq,getQual());
	}

	
	@Override
	public Sequence trimRight(int newLength,boolean color) {
		byte[] sq = getSeq();
		byte[] qv = this.getQual();
		int colorDiff = color ? 1 : 0;
		
		if (newLength <= sq.length) {
			return new SequenceQual(this.getId(),this.getComments(),
					Arrays.copyOf(sq, newLength),
					Arrays.copyOf(qv, newLength-colorDiff));
		} else {
			return this;
		}
	}
	
	@Override
	public double meanQuality() {
		UnivariableStat mean = new OnlineMean();
		for(int i=0;i<qual.length;i++) {
			mean.add(qual[i]);
		}
		return mean.get();
	}
}
