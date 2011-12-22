package bfx;

import java.io.File;
import java.io.IOException;

import bfx.io.impl.FileSequenceSink;

public abstract class SequenceSink {

	abstract public void write(Sequence seq) throws IOException;
		
	public static SequenceSink fromFile(String format,File file) {
		return new FileSequenceSink(format,file);
	}	

	public static SequenceSink fromFile(String format,String filename) {
		return new FileSequenceSink(format,new File(filename));
	}	
	
	public static SequenceSink fromFile(String format,File file1,File file2) {
		
		if (file2 != null && format != null && !format.equals("fasta")) {
			throw new RuntimeException("You can only use specify a qual file for fasta format");
		}
		
		if (file2==null)
			return new FileSequenceSink(format,file1);
		else
			return new FileSequenceSink(format,file1,file2);
	}		

	public static SequenceSink fromFile(String format,String filename1,String filename2) {
		return new FileSequenceSink(format,new File(filename1),filename2 == null ? null : new File(filename2));
	}		
}
