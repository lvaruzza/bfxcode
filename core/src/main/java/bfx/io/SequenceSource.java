package bfx.io;

import java.io.File;
import java.util.Iterator;

import bfx.ProgressCounter;
import bfx.Sequence;
import bfx.io.impl.FileSequenceSource;
import bfx.io.impl.KmerIterable;
import bfx.io.impl.KmerWithQualIterable;
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
	protected ProgressCounter pc;
	
	
	/**
	 * Set ProgressCounter to be increment on each sequence.
	 * 
	 * @param pc ProgressCounter.
	 */
	public void setProgressCounter(ProgressCounter pc) {
		this.pc = pc;
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
	 * @param seqfile Sequence File Name
	 * @param qualfile Quality File Name
	 * @return A FileSequenceSource
	 */
	public static SequenceSource fromFile(String format,String seqFilename,String qualFilename) {
		return new FileSequenceSource(format,new File(seqFilename),qualFilename == null ? null : new File(qualFilename));
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
