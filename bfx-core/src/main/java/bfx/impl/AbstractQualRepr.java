package bfx.impl;

import bfx.QualRepr;

public abstract class AbstractQualRepr implements QualRepr {


	public abstract byte[] qualToBytes(byte[] qual,int off,int len);

	public byte[] qualToBytes(byte[] qual) {
		return qualToBytes(qual,0,qual.length);
	}
	
	public String qualToString(byte[] qual) {
		return new String(qualToBytes(qual));
	}
	
	public abstract byte[] stringToQual(String repr);
	public abstract byte[] bytesToQual(byte[] repr,int off,int len); 
	
	public byte[] bytesToQual(byte[] repr) {
		return bytesToQual(repr,0,repr.length); 
	}
}
