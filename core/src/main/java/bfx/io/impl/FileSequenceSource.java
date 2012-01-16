package bfx.io.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import bfx.Sequence;
import bfx.exceptions.FileProcessingRuntimeException;
import bfx.exceptions.MultipleFilesProcessingRuntimeException;
import bfx.io.SequenceFormats;
import bfx.io.SequenceReader;
import bfx.io.SequenceSource;

public class FileSequenceSource extends SequenceSource {

	private File file1 = null;
	private File file2 = null;
	
	private SequenceReader reader;

	public FileSequenceSource(File file1,File file2) {
		setup(file1,file1);
	}
	
	private void setup(File file1,File file2) {
		this.file1 = file1;
		this.file2 = file2;
		reader = SequenceFormats.getReaderForFile(file1.getName());
	}

	public FileSequenceSource(String format,File file1,File file2) {
		setup(format,file1,file2);
	}
	
	public void setup(String format,File file1,File file2) {
		this.file1 = file1;
		this.file2 = file2;
		reader = SequenceFormats.getReaderForFile(file1.getName(),format);
	}
	
	public FileSequenceSource(File file1) {
		setup(file1);
	}
		
	public void setup(File file1) {
		this.file1 = file1;
		this.file2 = null;
		reader = SequenceFormats.getReaderForFile(file1.getName());
	}

	
	public FileSequenceSource(String format,File file1) {
		setup(format,file1);
	}
	
	public void setup(String format,File file1) {
		this.file1 = file1;
		this.file2 = null;
		reader = SequenceFormats.getReaderForFile(file1.getName(),format);
		reader.setProgressCounter(pc);
	}
	
	public FileSequenceSource(String filename) {
		setup(new File(filename));
	}

	public FileSequenceSource(String format,String filename1,String filename2) {
		if (filename2 == null)
			setup(format,new File(filename1));
		else
			setup(format,new File(filename1),new File(filename2));
	}
	
	@Override
	public Iterator<Sequence> iterator() {
		reader.setProgressCounter(pc);
		try {
			if (file2 == null) {
				return reader.read(file1);
			} else { 
				return reader.read(file1,file2);
			}
		} catch(IOException e) {
			if (file2 == null)
				throw new FileProcessingRuntimeException(e,file1);
			else
				throw new MultipleFilesProcessingRuntimeException(e,file1,file2);				
		}
	}
}
