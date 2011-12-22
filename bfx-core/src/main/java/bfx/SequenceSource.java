package bfx;

import java.io.File;
import java.util.Iterator;

import bfx.io.impl.FileSequenceSource;
import bfx.utils.IteratorUtils;

public abstract class SequenceSource implements Iterable<Sequence> {
	protected ProgressCounter pc;
	
	public void setProgressCounter(ProgressCounter pc) {
		this.pc = pc;
	}

	@Override
	abstract public Iterator<Sequence> iterator();
	
	public long count() {
		return IteratorUtils.count(iterator());
	}
	
	public static SequenceSource fromFile(String format,File file) {
		return new FileSequenceSource(format,file);
	}	

	public static SequenceSource fromFile(String format,String filename) {
		return new FileSequenceSource(format,new File(filename));
	}	
	
	public static SequenceSource fromFile(String format,File file1,File file2) {
		
		if (file2 != null && format != null && !format.equals("fasta")) {
			throw new RuntimeException("You can only use specify a qual file for fasta format");
		}
		
		if (file2==null)
			return new FileSequenceSource(format,file1);
		else
			return new FileSequenceSource(format,file1,file2);
	}		

	public static SequenceSource fromFile(String format,String filename1,String filename2) {
		return new FileSequenceSource(format,new File(filename1),filename2 == null ? null : new File(filename2));
	}		
}
