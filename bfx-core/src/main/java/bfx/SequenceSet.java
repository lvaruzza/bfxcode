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
		return new FileSequenceSet(format,file1,file2);
	}		

	public static SequenceSet fromFile(String format,String filename1,String filename2) {
		return new FileSequenceSet(format,new File(filename1),new File(filename2));
	}		
}
