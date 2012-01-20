package bfx.spectrum;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class SpectrumIO {
	public static byte[] fileSignature = "SPEC".getBytes();

	public static class SpectrumHeader {
		public int k;
		public long nkmers;
		
		public SpectrumHeader(int k, long nkmers) {
			this.k = k;
			this.nkmers = nkmers;
		}
		
		public SpectrumHeader() {}
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
		dos.write(fileSignature);
		dos.writeInt(spectrumHeader.k);
		dos.writeLong(spectrumHeader.nkmers);
	}
}
