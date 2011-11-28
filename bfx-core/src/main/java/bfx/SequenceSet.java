package bfx;

import java.io.File;
import java.util.Iterator;

import bfx.io.impl.FileSequenceSet;
import bfx.utils.IteratorUtils;

public abstract class SequenceSet implements Iterable<Sequence> {

	@Override
	abstract public Iterator<Sequence> iterator();
	
	public long count() {
		return IteratorUtils.count(iterator());
	}
	
	public static SequenceSet fromFile(String format,File file) {
		return new FileSequenceSet(format,file);
	}	

	public static SequenceSet fromFile(String format,String filename) {
		return new FileSequenceSet(format,new File(filename));
	}	
	
	public static SequenceSet fromFile(String format,File file1,File file2) {
		
		if (file2 != null && format != null && !format.equals("fasta")) {
			throw new RuntimeException("You can only use specify a qual file for fasta format");
		}
		
		if (file2==null)
			return new FileSequenceSet(format,file1);
		else
			return new FileSequenceSet(format,file1,file2);
	}		

	public static SequenceSet fromFile(String format,String filename1,String filename2) {
		return new FileSequenceSet(format,new File(filename1),filename2 == null ? null : new File(filename2));
	}		
}
