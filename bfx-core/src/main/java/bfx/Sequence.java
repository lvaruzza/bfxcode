package bfx;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import bfx.impl.FastaQualRepr;
import bfx.impl.SequenceConstQualImpl;

public abstract class Sequence {
	//private static Logger log = Logger.getLogger(Sequence.class);
	
	private String id;
	private String comments;
	
	public String getId() {
		return id;
	}
	
	public String getComments() {
		return comments;
	}
	
	public abstract byte[] getSeq();
	public abstract byte[] getQual();

	
	
	public Sequence(String id, String comments) {
		super();
		assert(id != null);
		assert(comments != null);
		
		this.id = id;
		this.comments = comments;
	}

	public Sequence(String header) {
		String[] h = parseHeader(header);
		id = h[0];
		comments = h[1];
	}
	
	public String getSeqAsString() {
		return new String(getSeq());
	}

	@Override
	public String toString() {
		return "Sequence [Id=" + getId() + ", seq=" +
				getSeqAsString() + ", qual="
				+ Arrays.toString(getQual()) + "]";
	}

	public static String[] parseHeader(String header) {
		int idx = header.indexOf(' ');
		if (idx != -1)
			return new String[] {header.substring(0, idx),header.substring(idx+1)};
		else
			return new String[] {header,""};			
	}

	public String digestSeq(String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.reset();
		return String.format("%032x",new BigInteger(1,digest.digest(getSeq())));
	}
	
	public String digestSeq() {
		try {
			return digestSeq("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public String digestQual(String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.reset();
		return String.format("%032x",new BigInteger(1,digest.digest(getQual())));
	}
	
	public String digestQual() {
		try {
			return digestQual("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public int length() {
		return this.getSeq().length;
	}

	
	private static QualRepr qrepr = new FastaQualRepr();
	
	public String getQualAsString() {
		return qrepr.qualToTextString(getQual());
	}
	
	public abstract Sequence changeSeq(byte[] seq);
	
	public static Sequence make(byte[] seq) {
		return new SequenceConstQualImpl("",seq,(byte)0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + Arrays.hashCode(getQual());
		result = prime * result + Arrays.hashCode(getSeq());
		return result;
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
	
	
}
