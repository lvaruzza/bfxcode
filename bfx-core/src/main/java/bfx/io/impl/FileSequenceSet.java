package bfx.io.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import bfx.Sequence;
import bfx.SequenceSet;
import bfx.exceptions.FileProcessingRuntimeException;
import bfx.exceptions.MultipleFilesProcessingRuntimeException;
import bfx.io.SequenceFormats;
import bfx.io.SequenceReader;

public class FileSequenceSet extends SequenceSet {

	private File file1 = null;
	private File file2 = null;
	
	private SequenceReader reader;
	
	public FileSequenceSet(File file1,File file2) {
		this.file1 = file1;
		this.file2 = file2;
		reader = SequenceFormats.getReader(file1.getName());
	}

	public FileSequenceSet(String format,File file1,File file2) {
		this.file1 = file1;
		this.file2 = file2;
		reader = SequenceFormats.getReader(file1.getName(),format);
	}
	
	public FileSequenceSet(File file1) {
		this.file1 = file1;
		this.file2 = null;
		reader = SequenceFormats.getReader(file1.getName());
	}

	
	public FileSequenceSet(String format,File file1) {
		this.file1 = file1;
		this.file2 = null;
		reader = SequenceFormats.getReader(file1.getName(),format);
	}
	
	public FileSequenceSet(String filename) {
		this(new File(filename));
	}

	public FileSequenceSet(String filename1,String filename2) {
		this(new File(filename1),new File(filename2));
	}
	
	@Override
	public Iterator<Sequence> iterator() {
		try {
			if (file2 == null)
				return reader.read(file1);
			else
				return reader.read(file2);				
		} catch(IOException e) {
			if (file2 == null)
				throw new FileProcessingRuntimeException(e,file1);
			else
				throw new MultipleFilesProcessingRuntimeException(e,file1,file2);				
		}
	}
}
