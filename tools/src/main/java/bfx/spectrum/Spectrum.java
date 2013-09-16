package bfx.spectrum;

import java.io.PrintStream;
import java.util.Iterator;

import bfx.process.ProgressMeter;

public abstract class Spectrum implements Iterable<Kmer>{
	protected int k;
	protected long nkmers;
	protected ProgressMeter pc;
	
	
	protected Spectrum(int k,long nkmers) {
		this.k = k;
		this.nkmers = nkmers;
	}

	protected Spectrum() {};
	
	public abstract boolean member(byte[] seq);
	public abstract long getCount(byte[] seq);
	public abstract Iterator<Kmer> iterator();	

	public void dump(PrintStream out) {
		for(Kmer kmer:this) {
			out.print(new String(kmer.kmer));
			out.print("\t");
			out.println(kmer.count);
			if (pc!=null) pc.incr(1);
			//log.debug(String.format("D: %s\t%d",new String(kmer.fst),kmer.snd));
		}
		out.flush();
	}

	protected void setK(int k) {
		this.k = k;
	}


	public void setProgressCounter(ProgressMeter pc) {
		this.pc = pc;
	}

	public int getK() {
		return k;
	}

	public long numberOfKmers() {
		return nkmers;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Spectrum))
			return false;
		Spectrum other = (Spectrum) obj;
		if (k != other.k)
			return false;
		if (nkmers != other.nkmers)
			return false;
		
		Iterator<Kmer> ia = this.iterator();
		Iterator<Kmer> ib = other.iterator();
		
		while(ia.hasNext() && ib.hasNext()) {
			Kmer ka = ia.next();
			Kmer kb = ib.next();
			if (!ka.equals(kb)) return false;
		}
		return true;
	}	
	
	
}
