package bfx.spectrum;

import java.io.PrintStream;
import java.util.Iterator;

import bfx.ProgressCounter;
import bfx.utils.Pair;

public abstract class Spectrum implements Iterable<Pair<byte[],Long>>{
	protected int k;
	protected long nkmers;
	protected ProgressCounter pc;
	
	public static byte[] fileSignature = "SPEC".getBytes();
	
	
	protected Spectrum(int k,long nkmers) {
		this.k = k;
		this.nkmers = nkmers;
	}

	protected Spectrum() {};
	
	public abstract boolean member(byte[] seq);
	public abstract long getCount(byte[] seq);
	public abstract Iterator<Pair<byte[], Long>> iterator();	

	public void dump(PrintStream out) {
		for(Pair<byte[],Long> kmer:this) {
			out.print(new String(kmer.fst));
			out.print("\t");
			out.println(kmer.snd);
			if (pc!=null) pc.incr(1);
			//log.debug(String.format("D: %s\t%d",new String(kmer.fst),kmer.snd));
		}
		out.flush();
	}

	protected void setK(int k) {
		this.k = k;
	}


	public void setProgressCounter(ProgressCounter pc) {
		this.pc = pc;
	}	
}
