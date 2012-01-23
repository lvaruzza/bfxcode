package bfx.spectrum;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

import bfx.spectrum.SpectrumIO.SpectrumHeader;

public class DiskSpectrum extends Spectrum {
	private File file;
	
	public DiskSpectrum(File file) throws IOException {
		this.file = file;
		SpectrumHeader h = SpectrumIO.readHeader(new RandomAccessFile(file,"r"));
		this.k = h.k;
		this.nkmers = h.nkmers;
	}

	public DiskSpectrum(String filename) throws IOException {
		this(new File(filename));
	}
	
	@Override
	public boolean member(byte[] seq) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public long getCount(byte[] seq) {
		throw new RuntimeException("Unimplemented");
	}
	
	public static class SpectrumDiskIterator implements Iterator<Kmer> {
		private RandomAccessFile input;
		private long pos;
		private long length;
		private SpectrumHeader header;
		
		public SpectrumDiskIterator(File file) throws IOException { 
			this.input = new RandomAccessFile(file,"r");
			length = input.length();
			header = SpectrumIO.readHeader(input);
			pos = input.getFilePointer();
		}

		@Override
		public boolean hasNext() {
			return pos < length;
		}

		@Override
		public Kmer next() {
			try {
				byte[] kmer = new byte[header.k];
				input.read(kmer);
				long count = input.readLong();
				pos += header.k + 8;
				//System.out.println(String.format("file=%d pos=%d",input.getFilePointer(),pos));
				return new Kmer(kmer,count);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void remove() {
			throw new RuntimeException("Unimplemented");
		}
	}
	
	@Override
	public Iterator<Kmer> iterator() {
		try {
			return new SpectrumDiskIterator(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
