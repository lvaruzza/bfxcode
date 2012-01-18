package bfx.spectrum;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import bfx.ProgressCounter;
import bfx.tools.Report;

public abstract class SpectrumBuilder {	
	//private static Logger log = Logger.getLogger(Spectrum.class);
	
	protected boolean finished = false;
	protected int k;
	protected long nkmers;
	protected ProgressCounter pc;
	
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
	
	public SpectrumBuilder(int k) {
		this.k = k;
	}

	protected SpectrumBuilder() {
		
	}
	
	protected abstract void add1(byte[] seq) throws IOException;
	
	public void add(byte [] seq) throws IOException {
		if(seq.length != k) throw new RuntimeException(String.format("Sequence size '%d' not equal to k value (%d)",seq.length,k));
		
		add1(seq);
	}
	
	protected void writeHeader(DataOutputStream dos) throws IOException {
		dos.write(Spectrum.fileSignature);
		dos.writeInt(k);
		dos.writeLong(nkmers);
	}
	
	
	public Report getReport() {
		SpectrumReport report = new SpectrumReport();
		report.k = k;
		report.nkmers = nkmers;
		report.kmerGenomeSize = nkmers * k;
		
		return report;
	}


	public void save(String output) throws IOException {
		OutputStream out = new FileOutputStream(output);
		save(out);
	}

	public abstract void save(OutputStream out) throws IOException;
	
	public abstract void finish();	
	
	public boolean isFinished() {
		return finished;
	}

	public void setProgressCounter(ProgressCounter pc) {
		this.pc = pc;
	}
}
