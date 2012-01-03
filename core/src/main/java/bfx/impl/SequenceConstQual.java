package bfx.impl;

import java.util.Arrays;

import bfx.Sequence;

/**
 * Representation of Sequence object with text and a constant value
 * for the quality.
 *  
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class SequenceConstQual extends Sequence {
	private byte[] seq;
	private byte qual;
	
	/**
	 * Construct a new Sequence Object
	 * 
	 * @param header Header string that will be parsed in ID and Comments
	 * @param seq Sequence byte array
	 * @param qual Constant sequence quality value
	 * 
	 */
	public SequenceConstQual(String header,byte[] seq, byte qual) {
		super(header);
		this.seq = seq;
		this.qual = qual;
	}

	/**
	 * Construct a new Sequence Object
	 * 
	 * @param id Sequence ID
	 * @param comments Comments
	 * @param seq byte array with Sequence Text
	 * @param qual Constant Quality Value
	 */
	public SequenceConstQual(String id,String comments,byte[] seq, byte qual) {
		super(id,comments);
		this.seq = seq;
		this.qual = qual;
	}
	
	/**
	 * Construct a new Sequence Object
	 *
	 * @param header Sequence header
	 * @param seq Sequence Text as String
	 * @param qual Constant Quality Value
	 */
	public SequenceConstQual(String header,String seq, byte qual) {
		this(header,seq.getBytes(),qual);
	}


	@Override
	public byte[] getSeq() {
		return seq;
	}
	
	@Override
	public byte[] getQual() {
		byte[] q = new byte[seq.length];
		Arrays.fill(q, qual);
		return q;
	}
	
	@Override
	public Sequence changeSeq(byte[] newseq) {
		return new SequenceConstQual(this.getId(),this.getComments(),newseq,qual);
	}
	
	@Override
	public String toString() {
		return "Sequence [Id=" + getId() + ", seq=" +
				getSeqAsString() + ", qual=" + qual + "]";
	}
}
