package bfx;

/**
 * 
 * Convert between Sequence Quality values representations and 
 * byte array of quality values.
 * 
 * Concrete implementations inherit from this class
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 * 
 */
public abstract class QualRepr {


	/**
	 * Convert Qual Values to text represetation
	 * 
	 * @param qual Quality values.
	 * @param off Vector offset.
	 * @param len length of quality values in qual vector.
	 * @return byte array with text representation.
	 */
	public abstract byte[] qualToTextBytes(byte[] qual,int off,int len);

	
	public byte[] qualToTextBytes(byte[] qual) {
		return qualToTextBytes(qual,0,qual.length);
	}
	
	public String qualToTextString(byte[] qual) {
		return new String(qualToTextBytes(qual));
	}
	
	public abstract byte[] textToQual(String repr);
	
	
	/**
	 * Convert Text Representation to quality values
	 * 
	 * @param repr Byte array with text representation
	 * @param off Offset in repr
	 * @param len Length of text
	 * @return Byte array with quality values
	 */
	public abstract byte[] textToQual(byte[] repr,int off,int len); 
	
	public byte[] textToQual(byte[] repr) {
		return textToQual(repr,0,repr.length); 
	}
}
