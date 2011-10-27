package bfx;

/*
 * Sequence Quality values text representations
 * 
 */
public interface QualRepr {
	public byte[] bytesToQual(byte[] repr);
	public byte[] bytesToQual(byte[] repr,int off,int len);
	public byte[] stringToQual(String repr);

	public String qualToString(String qual);
	public byte[] qualToBytes(byte[] qual);	
	public byte[] qualToBytes(byte[] qual,int off,int len);	
}
