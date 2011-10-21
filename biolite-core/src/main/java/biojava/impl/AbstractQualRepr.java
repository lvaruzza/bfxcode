package biojava.impl;

import biolite.QualRepr;

public abstract class AbstractQualRepr implements QualRepr {

	public abstract byte[] qualToBytes(byte[] qual);
	
	public String qualToString(String qual) {
		return new String(qualToBytes(qual.getBytes()));
	}
	
	public abstract byte[] stringToQual(String repr);
	public abstract byte[] bytesToQual(byte[] repr); 
}
