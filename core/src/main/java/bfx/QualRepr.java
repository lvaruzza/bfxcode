package bfx;

/*
 * Convert between Sequence Quality values representations and 
 * byte array of quality values.
 * 
 * Concrete implementations inherit from this class
 * 
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
