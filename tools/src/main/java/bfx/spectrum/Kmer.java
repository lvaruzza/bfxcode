package bfx.spectrum;

public class Kmer {
	public byte[] kmer;
	public long count;
	
	public Kmer(byte[] kmer,long count) {
		this.kmer = kmer;
		this.count = count;
	}
}
