package bfx.impl;



/*
 * Encode and decode quality values in FastQ
 * 
 */
public class FastqRepr extends AbstractQualRepr {
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

	public FastqRepr() {
		encoding = FastqEncoding.SANGER;
	}

	public FastqRepr(FastqEncoding encoding) {
		this.encoding = encoding;
	}

	@Override
	public byte[] qualToBytes(byte[] qual, int off, int len) {
		byte[] out = new byte[len];
		for(int i=0;i<len;i++) {
			out[i] = (byte)(qual[off+i] + encoding.getEncodingStart());
		}
		return out;
	}

	public byte[] stringToQual(String repr) {
		return bytesToQual(repr.getBytes());
	}
	
	public byte[] bytesToQual(byte[] repr) {
		byte[] qual = new byte[repr.length];
		for(int i=0;i<qual.length;i++) {
			qual[i] = (byte)(qual[i] - encoding.getEncodingStart());
		}
		return qual;		
	}

	@Override
	public byte[] bytesToQual(byte[] repr, int off, int len) {
		// TODO Auto-generated method stub
		return null;
	}
}
