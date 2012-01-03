package bfx;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import bfx.impl.FastaQualRepr;
import bfx.impl.SequenceConstQual;
import bfx.impl.SequenceQual;

/**
 * 
 * Abstract base class for all sequence formats
 * 
 * A sequence has the sequence text and quality values, both the text
 * and qualities are represented as byte arrays.
 * 
 * This class hierarchy does not care about the symbols in the sequence, there is
 * classes in package bfx.seqenc to deal with specific sequences (DNA, color-space DNA, etc).
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *  
 */
/**
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public abstract class Sequence {
	//private static Logger log = Logger.getLogger(Sequence.class);
	
	private String id;
	private String comments;
	
	/**
	 * The ID should be unique in a given sequence set.
	 * 
	 * This field is equivalent of the first part of a fasta header
	 * 
	 * @return Sequence ID
	 */
	public String getId() {
		return id;
	}

	
	/**
	 * Other comments about the sequence
	 * 
	 * This is equivalent of the second part of the fasta header.
	 * 
	 * @return comments
	 */
	public String getComments() {
		return comments;
	}
	
	/**
	 * Get Sequence Text. 
	 * 
	 * The returned value should not be modified.
	 * 
	 * @return byte[] with sequence text.
	 */
	public abstract byte[] getSeq();
	
	
	/**
	 * Get Quality values
	 * 
	 * The returned value should not be modified.
	 * 
	 * @return byte[] with quality values.
	 * 
	 */
	public abstract byte[] getQual();

	
	
	/**
	 * Constructs a empty sequence with ID and comments.
	 * 
	 * Both arguments can not be null.
	 * 
	 * Should be used by concrete implementations of this class.
	 * 
	 * @param id		Sequence unique ID
	 * @param comments	Extra comments about the sequence
	 */
	protected Sequence(String id, String comments) {
		super();
		assert(id != null);
		assert(comments != null);
		
		this.id = id;
		this.comments = comments;
	}

	/**
	 * Construct a empty Sequence with header.
	 * 
	 * Splits the header between the ID and the comments using
	 * the static method parseHeader.
	 * 
	 * Header can not be null.
	 * 
	 * Should be used by concrete implementations of this class.
	 * 
	 * @param header
	 */
	protected Sequence(String header) {
		assert(header != null);
		String[] h = parseHeader(header);
		id = h[0];
		comments = h[1];
	}
	
	/**
	 * Utility method to return the sequence text as a String
	 * 
	 * @return getSeq() converted to a String
	 */
	public String getSeqAsString() {
		return new String(getSeq());
	}

	@Override
	public String toString() {
		return "Sequence [Id=" + getId() + ", seq=" +
				getSeqAsString() + ", qual="
				+ Arrays.toString(getQual()) + "]";
	}

	/**
	 * Split the header between the ID and comments using the 
	 * fasta convetion. 
	 * 
	 * For example, this header
	 * gi|224589808:41196312-41277500 Homo sapiens chromosome 17, GRCh37.p5 Primary Assembly
	 * 
	 * will be splited into
	 *   ["gi|224589808:41196312-41277500","Homo sapiens chromosome 17, GRCh37.p5 Primary Assembly"]
	 *    
	 * @param header Sequence Header
	 * @return A String[2] with ID in the first element and comments in the second. If there not comments
	 * the second element will be a empty string.
	 * 
	 */
	public static String[] parseHeader(String header) {
		int idx = header.indexOf(' ');
		if (idx != -1)
			return new String[] {header.substring(0, idx),header.substring(idx+1)};
		else
			return new String[] {header,""};			
	}

	/**
	 * Apply a Message Digest Algorithm (such MD5 or SHA-1) the sequence text and return
	 * a hexadecimal string representing the hash value.
	 * 
	 * @param algorithm Name of the algorithm
	 * @return A string with the hexadecimal representation of the hash value
	 * @throws NoSuchAlgorithmException
	 */
	public String digestSeq(String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.reset();
		return String.format("%032x",new BigInteger(1,digest.digest(getSeq())));
	}
	
	
	/**
	 * Apply digestSeq using MD5 sum.
	 * 
	 * @return A string with a hexadecimal representation of MD5 value.
	 * 
	 */
	public String digestSeq() {
		try {
			return digestSeq("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Apply a Message Digest Algorithm (such MD5 or SHA-1) the quality values (the bytes, not the string representation) 
	 * and return a hexadecimal string representing the hash value.
	 * 
	 * @param algorithm Name of the algorithm
	 * @return A string with the hexadecimal representation of the hash value
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	public String digestQual(String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.reset();
		return String.format("%032x",new BigInteger(1,digest.digest(getQual())));
	}
	
	/**
	 * Apply digestQual using MD5 sum.
	 * 
	 * @return A string with a hexadecimal representation of MD5 value.
	 * 
	 */
	public String digestQual() {
		try {
			return digestQual("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return Sequence Length
	 */
	public int length() {
		return this.getSeq().length;
	}

	
	private static QualRepr qrepr = new FastaQualRepr();
	
	/**
	 * FastaQual representation of quality values.
	 * 
	 * @return Qualities values as a String using FastaQual representation
	 * 
	 */
	public String getQualAsString() {
		return qrepr.qualToTextString(getQual());
	}
	
	/**
	 * Create a new Sequence object with the new sequence text. Other values are copied from this.
	 * 
	 * @param text New sequence Text
	 * @return a new Sequence object
	 */
	public abstract Sequence changeSeq(byte[] text);
	
	
	// Imutable object
	private int hashCode = 0;

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			final int prime = 31;
			hashCode = 1;
			hashCode = prime * hashCode
					+ ((comments == null) ? 0 : comments.hashCode());
			hashCode = prime * hashCode + ((id == null) ? 0 : id.hashCode());
			hashCode = prime * hashCode + Arrays.hashCode(getQual());
			hashCode = prime * hashCode + Arrays.hashCode(getSeq());
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sequence other = (Sequence) obj;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (!Arrays.equals(getSeq(), other.getSeq()))
			return false;
		if (!Arrays.equals(getQual(), other.getQual()))
			return false;
		return true;
	}

	/**
	 * Helper to create a new Sequence Object with empty header and 0 quality.
	 * 
	 * @param text Sequence text
	 * @return a new Sequence Object.
	 */
	public static Sequence make(byte[] text) {
		return new SequenceConstQual("",text,(byte)0);
	}

	
	/**
	 * Helper to create a new Sequence Object with empty header and 0 quality.
	 * 
	 * @param text Sequence text
	 * @return a new Sequence Object.
	 */
	public static Sequence make(String text) {
		return make(text.getBytes());
	}

	/**
	 * Helper to create a new Sequence Object with empty header.
	 * 
	 * @param text Sequence text.
	 * @param qual Sequence Qualities in fastaQual encoding.
	 * @return a new Sequence Object.
	 */
	public static Sequence make(String text, String qual) {
		return new SequenceQual("", text, qual);
	}	
}
