package bfx;

import java.util.Map;

public class GFF {
	private String seqid;
	private String source;
	private String type;
	
	private long start;
	private long end;
	private double score;
	
	private char strand;
	private byte phase;	
	private Map<String,String> attributes;
	
	public String getSeqid() {
		return seqid;
	}
	public String getSource() {
		return source;
	}
	public String getType() {
		return type;
	}
	public long getStart() {
		return start;
	}	

	public long getEnd() {
		return end;
	}
	public char getStrand() {
		return strand;
	}
	public byte getPhase() {
		return phase;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}

	public double getScore() {
		return score;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + (int) (end ^ (end >>> 32));
		result = prime * result + phase;
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((seqid == null) ? 0 : seqid.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + (int) (start ^ (start >>> 32));
		result = prime * result + strand;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		GFF other = (GFF) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (end != other.end)
			return false;
		if (phase != other.phase)
			return false;
		if (Double.doubleToLongBits(score) != Double
				.doubleToLongBits(other.score))
			return false;
		if (seqid == null) {
			if (other.seqid != null)
				return false;
		} else if (!seqid.equals(other.seqid))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (start != other.start)
			return false;
		if (strand != other.strand)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	public GFF(String seqid, String source, String type, long start,
			long end, double score,char strand, byte phase, Map<String, String> attributes) {
		super();
		this.seqid = seqid;
		this.source = source;
		this.type = type;
		this.start = start;
		this.end = end;
		this.score = score;
		this.strand = strand;
		this.phase = phase;
		this.attributes = attributes;
	}
	
	public String getAttribute(String name) {
		return attributes.get(name);
	}	
}
