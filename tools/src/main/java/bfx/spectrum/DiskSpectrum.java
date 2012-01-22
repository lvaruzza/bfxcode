package bfx.spectrum;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Comparator;
import java.util.Iterator;

import bfx.spectrum.SpectrumIO.SpectrumHeader;
import bfx.utils.ByteUtils;

public class DiskSpectrum extends Spectrum {
	private static Comparator<byte[]> cmp = new ByteUtils.BytesComparator();
	
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

	public static void merge(File fileOut,Spectrum a,Spectrum b) throws IOException {
		if (a.getK() != b.getK()) throw new RuntimeException("Can't merge spectrums, different values of k");
		
		DataOutputStream out = new DataOutputStream(new FileOutputStream(fileOut));
		
		SpectrumIO.writeHeader(out, new SpectrumHeader(a.getK(),-1));
		
		Iterator<Kmer> ia = a.iterator();
		Iterator<Kmer> ib = b.iterator();
		long nkmers = 0;
		Kmer ka = null;
		Kmer kb = null;
		while((ia.hasNext() || ka!=null) && (ib.hasNext() || kb!=null)) {
			if (ka == null && ia.hasNext()) ka = ia.next();
			if (kb == null && ib.hasNext()) kb = ib.next();
			System.out.print(String.format("ka = %s kb = %s\t",ka,kb));
			
			switch(cmp.compare(ka.kmer, kb.kmer)) {
			case 0:  
				Kmer r = new Kmer(ka.kmer,ka.count+kb.count); 
				System.out.println(String.format("%s+%s=%s",ka,kb,r));
				SpectrumIO.writeKmer(out, r);
				nkmers++;
				ka=null;
				kb=null;
				break;
			case 1:	 
				SpectrumIO.writeKmer(out,kb); 
				System.out.println(String.format("A: %s > %s",ka,kb));
				nkmers++;
				kb=null;
				break;
			case -1:
				SpectrumIO.writeKmer(out,ka); 
				System.out.println(String.format("B: %s < %s",ka,kb));
				nkmers++;
				ka=null;
				break;
			}
		}
		System.out.println(String.format("ka = %s kb = %s\t",ka,kb));
		if (ka!=null) SpectrumIO.writeKmer(out,ka); 
		if (kb!=null) SpectrumIO.writeKmer(out,kb); 
		
		Iterator<Kmer> rest;
		if (ia.hasNext()) {
			System.out.println("ia has next");
			rest = ia;
		} else {
			System.out.println("ib has next");
			rest = ib;			
		}
		while(rest.hasNext()) {
			Kmer r = rest.next();
			System.out.println(String.format("%s in rest",r));
			out.write(r.kmer);
			out.writeLong(r.count);	
			nkmers++;
		}
		out.close();
		SpectrumIO.fixNkmers(fileOut,nkmers);
	}
}
