package bfx.spectrum;

import java.util.Arrays;

public class Kmer {
	public byte[] kmer;
	public long count;
	
	public Kmer(byte[] kmer,long count) {
		this.kmer = kmer;
		this.count = count;
	}
	
	@Override
	public String toString() {
		return String.format("<%s,%d>",new String(kmer),count);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (count ^ (count >>> 32));
		result = prime * result + Arrays.hashCode(kmer);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Kmer))
			return false;
		Kmer other = (Kmer) obj;
		if (count != other.count)
			return false;
		if (!Arrays.equals(kmer, other.kmer))
			return false;
		return true;
	}
}
