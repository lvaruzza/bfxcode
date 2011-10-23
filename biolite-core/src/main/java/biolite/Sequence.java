package biolite;

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
	
	
}
