package bfx;

/*
 * Sequence Quality values text representations
 * 
 * TODO: Do I need all this variants?
 */
public interface QualRepr {
	
	/*
	 * Convert text to a byte array with the qual values
	 */
	public byte[] textToQual(byte[] repr);
	public byte[] textToQual(byte[] repr,int off,int len);
	public byte[] textToQual(String repr);

	/*
	 * Convert from qual byte array to text
	 * 
	 */
	public String qualToTextString(byte[] qual);
	public byte[] qualToTextBytes(byte[] qual);	
	public byte[] qualToTextBytes(byte[] qual,int off,int len);	
}
