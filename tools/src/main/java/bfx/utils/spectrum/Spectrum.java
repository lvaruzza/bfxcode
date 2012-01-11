package bfx.utils.spectrum;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import bfx.tools.Report;
import bfx.utils.Pair;

public abstract class Spectrum implements Iterable<Pair<byte[],Long>> {
	protected int k;
	
	public Spectrum(int k) {
		this.k = k;
	}
	public abstract void add(byte [] seq);
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
		return null;
	}
}
