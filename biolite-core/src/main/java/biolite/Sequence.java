package biolite;

import java.util.Arrays;

public abstract class Sequence {
	public abstract String getId();
	public abstract byte[] getSeq();
	public abstract byte[] getQual();

	public String getSeqAsString() {
		return new String(getSeq());
	}

	@Override
	public String toString() {
		return "Sequence [Id=" + getId() + ", seq=" +
				getSeqAsString() + ", qual="
				+ Arrays.toString(getQual()) + "]";
	}
	
	
}
