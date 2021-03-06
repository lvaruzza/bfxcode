package bfx.impl;

import bfx.QualRepr;



/**
 * Encode and decode quality values in the FastQ format
 * 
 * Supports Sanger, Illumina10 and Illumina13 formats.
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class FastQRepr extends QualRepr {
	public enum FastqEncoding  {
		SANGER(33,0,93),
		ILLUMINA10(59+5,-5,62),
		ILLUMINA13(64,0,62);
		
		private byte encodigStart;
		private byte minValue;
		private byte maxValue;
		
		public byte getEncodingStart() { return encodigStart; };
		public byte getMinValue() { return minValue; };
		public byte getMaxValue() { return maxValue; };
		
		private FastqEncoding(int start,int min,int max) {
			minValue = (byte)min;
			maxValue = (byte)max;
			encodigStart = (byte)start;
		}
	};

	private FastqEncoding encoding = FastqEncoding.SANGER;

	/**
	 * Create a FastQ Quality Representation using default Sanger encoding.
	 * 
	 */
	public FastQRepr() {
		encoding = FastqEncoding.SANGER;
	}

	
	/**
	 * Create a FAstQ Quality Representation using given Encoding.
	 * 
	 * @param encoding
	 */
	public FastQRepr(FastqEncoding encoding) {
		this.encoding = encoding;
	}

	@Override
	public byte[] qualToTextBytes(byte[] qual, int off, int len) {
		byte[] out = new byte[len];
		for(int i=0;i<len;i++) {
			out[i] = (byte)(qual[off+i] + encoding.getEncodingStart());
		}
		return out;
	}

	@Override
	public byte[] textToQual(String repr) {
		return textToQual(repr.getBytes());
	}
	
	@Override
	public byte[] textToQual(byte[] repr) {
		byte[] qual = new byte[repr.length];
		for(int i=0;i<qual.length;i++) {
			qual[i] = (byte)(repr[i] - encoding.getEncodingStart());
		}
		return qual;		
	}

	@Override
	public byte[] textToQual(byte[] repr, int off, int len) {
		// TODO Auto-generated method stub
		return null;
	}
}
