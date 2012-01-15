package bfx.spectrum;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;

import bfx.tools.Report;
import bfx.utils.Pair;

public abstract class Spectrum implements Iterable<Pair<byte[],Long>> {	
	private static Logger log = Logger.getLogger(Spectrum.class);
	
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

	protected Spectrum() {
		
	}
	
	protected abstract void add1(byte[] seq);
	
	public void add(byte [] seq) {
		if(seq.length != k) throw new RuntimeException(String.format("Sequence size '%d' not equal to k value (%d)",seq.length,k));
		
		add1(seq);
	}
	
	public abstract boolean member(byte[] seq);
	public abstract long getCount(byte[] seq);
	public abstract Iterator<Pair<byte[], Long>> iterator();
		
	private static byte[] signature = "SPEC".getBytes();
	
	protected void writeHeader(DataOutputStream dos) throws IOException {
		dos.write(signature);
		dos.writeInt(k);
		dos.writeLong(nkmers);
	}
	
	protected void setK(int k) {
		this.k = k;
	}
	
	protected void readHeader(DataInputStream dis) throws IOException {
		byte[] header = new byte[4];
		dis.read(header);
		if (!Arrays.equals(header,signature))
			throw new RuntimeException("Invalid spectrum file, file signature does not match");
		setK(dis.readInt());
		nkmers = dis.readLong();
	}
	
	public Report getReport() {
		SpectrumReport report = new SpectrumReport();
		report.k = k;
		report.nkmers = nkmers;
		report.kmerGenomeSize = nkmers * k;
		
		return report;
	}

	public void dump(PrintStream out) {
		for(Pair<byte[],Long> kmer:this) {
			out.print(new String(kmer.fst));
			out.print("\t");
			out.println(kmer.snd);
			//log.debug(String.format("D: %s\t%d",new String(kmer.fst),kmer.snd));
		}
		out.flush();
	}

	public void save(String output) throws IOException {
		OutputStream out = new FileOutputStream(output);
		save(out);
	}

	public abstract void save(OutputStream out) throws IOException;
}
