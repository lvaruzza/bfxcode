package bfx.utils.spectrum;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import bfx.tools.Report;
import bfx.utils.Pair;

public abstract class Spectrum implements Iterable<Pair<byte[],Long>> {	
	static public class SpectrumReport extends Report {
		public long nkmers;
		public long kmerGenomeSize;
		public int k;
		
		@Override
		public void writeHuman(PrintWriter out) {
			out.println(String.format("\tk                \t%d",k));			
			out.println(String.format("\tn kmers          \t%,d",nkmers));			
			out.println(String.format("\tkmers genome size\t%,d",kmerGenomeSize));			
		}
		
	}
	
	protected int k;
	protected long nkmers;
	
	public Spectrum(int k) {
		this.k = k;
	}
	
	protected abstract void add1(byte[] seq);
	
	public void add(byte [] seq) {
		if(seq.length != k) throw new RuntimeException(String.format("Sequence size '%d' not equal to k value (%d)",seq.length,k));
		
		add1(seq);
		nkmers++;
	}
	
	public abstract boolean member(byte[] seq);
	public abstract long getCount(byte[] seq);
	public abstract Iterator<Pair<byte[], Long>> iterator();

		
	public void save(String output) throws IOException {
		OutputStream out = new FileOutputStream(output);
		save(out);
	}
	
	private void writeHeader(DataOutputStream dos) throws IOException {
		dos.write("SPEC".getBytes());
		dos.writeInt(k);		
	}
	
	public void save(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		writeHeader(dos);
		for(Pair<byte[],Long> pair: this) {
			dos.write(pair.fst);
			dos.writeLong(pair.snd);
		}
	};
	
	public Report getReport() {
		SpectrumReport report = new SpectrumReport();
		report.k = k;
		report.nkmers = nkmers;
		report.kmerGenomeSize = nkmers * k;
		
		return report;
	}
}
