package bfx.spectrum;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import org.apache.log4j.Logger;

import bfx.utils.ByteUtils;

public class SpectrumIO {
	private static Logger log = Logger.getLogger(SpectrumIO.class);
	private static Comparator<byte[]> cmp = new ByteUtils.BytesComparator();
		
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
		//log.debug("Saving header " + spectrumHeader);

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
	
	public static void merge(File fileOut,Spectrum a,Spectrum b) throws IOException {
		if (a.getK() != b.getK()) throw new RuntimeException("Can't merge spectrums, different values of k");
		log.debug(String.format("Mering spectrums into '%s'",fileOut.getPath()));
		
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
			//System.out.print(String.format("ka = %s kb = %s\t",ka,kb));
			
			switch(cmp.compare(ka.kmer, kb.kmer)) {
			case 0:  
				Kmer r = new Kmer(ka.kmer,ka.count+kb.count); 
				//System.out.println(String.format("%s+%s=%s",ka,kb,r));
				SpectrumIO.writeKmer(out, r);
				nkmers++;
				ka=null;
				kb=null;
				break;
			case 1:	 
				SpectrumIO.writeKmer(out,kb); 
				//System.out.println(String.format("A: %s > %s",ka,kb));
				nkmers++;
				kb=null;
				break;
			case -1:
				SpectrumIO.writeKmer(out,ka); 
				//System.out.println(String.format("B: %s < %s",ka,kb));
				nkmers++;
				ka=null;
				break;
			}
		}
		//System.out.println(String.format("ka = %s kb = %s\t",ka,kb));
		if (ka!=null) {
			SpectrumIO.writeKmer(out,ka); 
			nkmers++;
		}
		if (kb!=null) {
			SpectrumIO.writeKmer(out,kb); 
			nkmers++;
		}
		
		Iterator<Kmer> rest;
		if (ia.hasNext()) {
			//System.out.println("ia has next");
			rest = ia;
		} else {
			//if (ib.hasNext()) System.out.println("ib has next");
			rest = ib;			
		}  
		while(rest.hasNext()) {
			Kmer r = rest.next();
			SpectrumIO.writeKmer(out, r);
			//System.out.println(String.format("%s in rest",r));
			nkmers++;
		}
		out.close();
		SpectrumIO.fixNkmers(fileOut,nkmers);
	}


	public static Kmer readKmer(DataInput input, int k) throws IOException {
		byte[] kmer = new byte[k];
		input.readFully(kmer);
		long count = input.readLong();
		return new Kmer(kmer,count);
	}	
}
