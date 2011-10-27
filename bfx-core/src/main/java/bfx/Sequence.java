package bfx;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.log4j.Logger;

public abstract class Sequence {
	private static Logger log = Logger.getLogger(Sequence.class);
	
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
		log.debug(String.format("'%s' = %s",header,Arrays.toString(h)));
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
	
}
