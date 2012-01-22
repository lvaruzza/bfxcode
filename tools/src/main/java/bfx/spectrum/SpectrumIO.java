package bfx.spectrum;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class SpectrumIO {
	private static Logger log = Logger.getLogger(SpectrumIO.class);
	
	public static byte[] fileSignature = "SPEC".getBytes();

	public static class SpectrumHeader {
		public int k;
		public long nkmers;
		
		public SpectrumHeader(int k, long nkmers) {
			this.k = k;
			this.nkmers = nkmers;
		}
		
		public SpectrumHeader() {}
		
		@Override
		public String toString() {
			return String.format("(SpectrumHeader k:%d nkmers:%d)",k,nkmers);
		}
	}
	
	public static SpectrumHeader readHeader(DataInput dis) throws IOException {
		byte[] sig = new byte[4];
		SpectrumHeader header = new SpectrumHeader();
		
		dis.readFully(sig);
		if (!Arrays.equals(sig,fileSignature))
			throw new RuntimeException("Invalid spectrum file, file signature does not match");
		header.k = dis.readInt();
		header.nkmers = dis.readLong();
		
		return header;
	}
	
	
	public static void writeHeader(DataOutputStream dos,SpectrumHeader spectrumHeader) throws IOException {
		log.debug("Saving header " + spectrumHeader);

		dos.write(fileSignature);
		dos.writeInt(spectrumHeader.k);
		dos.writeLong(spectrumHeader.nkmers);
	}

	public static void writeKmer(DataOutputStream out,Kmer kmer) throws IOException {
		out.write(kmer.kmer);
		out.writeLong(kmer.count);
	}
	
	public static void fixNkmers(File file, long nkmers) throws IOException {
		RandomAccessFile out = new RandomAccessFile(file, "rw");
		out.seek(fileSignature.length + 4);
		out.writeLong(nkmers);
		/*out.seek(0);
		SpectrumHeader h = readHeader(out);
		log.debug("Fixed header = " + h.toString());*/
		out.close();
	}
}
