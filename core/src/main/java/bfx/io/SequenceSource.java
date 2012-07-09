package bfx.io;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import bfx.Sequence;
import bfx.io.impl.BufferSequenceSource;
import bfx.io.impl.FileSequenceSource;
import bfx.io.impl.InputStreamSequenceSource;
import bfx.io.impl.KmerIterable;
import bfx.io.impl.KmerWithQualIterable;
import bfx.process.ProgressMeter;
import bfx.utils.BFXIteratorUtils;

/**
 * Abstract Sequence Iterable
 * 
 * This is an abstract class to iterate throught sets of sequences.
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public abstract class SequenceSource implements Iterable<Sequence> {
	protected ProgressMeter pm;
	
	
	/**
	 * Set ProgressCounter to be increment on each sequence.
	 * 
	 * @param pc ProgressCounter.
	 */
	public void setProgressMeter(ProgressMeter pc) {
		this.pm = pc;
	}

	@Override
	abstract public Iterator<Sequence> iterator();
	
	
	/**
	 * Count the number of sequences in this source.
	 * 
	 * @return Number of sequences
	 */
	public long count() {
		return BFXIteratorUtils.count(iterator());
	}
	
	/**
	 * Create a SequenceSource backed by a file.
	 * 
	 * @param format File Format (if null the format will be determined by the file name extension)
	 * @param file File
	 * @return A FileSequenceSource
	 */
	public static SequenceSource fromFile(String format,File file) {
		return new FileSequenceSource(format,file);
	}	

	/**
	 * Create a SequenceSource backed by a file.
	 * 
	 * @param format File Format (if null the format will be determined by the file name extension)
	 * @param filename File Name
	 * @return A FileSequenceSource
	 */
	public static SequenceSource fromFile(String format,String filename) {
		return new FileSequenceSource(format,new File(filename));
	}	

	/**
	 * Create a SequenceSource backed by a file.
	 * 
	 * @param filename File Name
	 * @return A FileSequenceSource
	 */
	public static SequenceSource fromFile(String filename) {
		return new FileSequenceSource(new File(filename));
	}	

	
	public static SequenceSource fromFileOrStdin(String format,String filename) {
		if (filename == null || filename.equals("-"))
			return new InputStreamSequenceSource(format,System.in);
		else
			return new FileSequenceSource(format,filename);
	}

	public static SequenceSource fromStream(String format,InputStream input) {
		return new InputStreamSequenceSource(format,input);		
	}

	
	/**
	 * Create a SequenceSource backed by a file.
	 * 
	 * @param file File
	 * @return A FileSequenceSource
	 */
	public static SequenceSource fromFile(File file) {
		return new FileSequenceSource(file);
	}	
	
	/**
	 * Create a SequenceSource backed by a file.
	 * 
	 * @param format File Format (if null the format will be determined by the file name extension)
	 * @param seqfile Sequence File
	 * @param qualfile Quality File
	 * @return A FileSequenceSource
	 */
	public static SequenceSource fromFile(String format,File seqfile,File qualfile) {
		
		if (qualfile != null && format != null && !format.equals("fasta")) {
			throw new RuntimeException("You can only use specify a qual file for fasta format");
		}
		
		if (qualfile==null)
			return new FileSequenceSource(format,seqfile);
		else
			return new FileSequenceSource(format,seqfile,qualfile);
	}		

	/**
	 * Create a SequenceSource backed by a file.
	 * 
	 * @param format File Format (if null the format will be determined by the file name extension)
	 * @param seqFilename Sequence File Name
	 * @param qualFilename Quality File Name
	 * @return A FileSequenceSource
	 */
	public static SequenceSource fromFile(String format,String seqFilename,String qualFilename) {
		return new FileSequenceSource(format,new File(seqFilename),qualFilename == null ? null : new File(qualFilename));
	}
	

	public static SequenceSource fromString(String format,String buffer) {
		return new BufferSequenceSource(format,buffer.getBytes());
	}
	
	
	public Iterable<byte[]> kmers(int k) {
		return kmers(k,0,0);
	}

	public Iterable<byte[]> kmersWithQual(int k) {
		return kmersWithQual(k,0,0);
	}
	
	public Iterable<byte[]> kmers(int k,int trimLeft,int trimRight) {
		return new KmerIterable(this,k,trimLeft,trimRight);
	}
	
	public Iterable<byte[]> kmersWithQual(int k,int trimLeft,int trimRight) {
		return new KmerWithQualIterable(this,k,trimLeft,trimRight);		
	}
}
