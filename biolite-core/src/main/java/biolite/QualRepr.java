package biolite;

/*
 * Sequence Quality values text representations
 * 
 */
public interface QualRepr {
	public byte[] bytesToQual(byte[] repr);
	public byte[] stringToQual(String repr);

	public String qualToString(String qual);
	public byte[] qualToBytes(byte[] qual);	
}
