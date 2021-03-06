package bfx.io.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import bfx.Sequence;
import bfx.exceptions.FileProcessingRuntimeException;
import bfx.exceptions.MultipleFilesProcessingRuntimeException;
import bfx.io.SequenceFormat;
import bfx.io.SequenceReader;
import bfx.io.SequenceSource;
import bfx.utils.compression.CompressionUtils;

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
		reader = SequenceFormat.getReaderForFile(file1.getName());
	}

	public FileSequenceSource(String format,File file1,File file2) {
		setup(format,file1,file2);
	}
	
	public void setup(String format,File file1,File file2) {
		this.file1 = file1;
		this.file2 = file2;
		reader = SequenceFormat.getReaderForFile(file1.getName(),format);
	}
	
	public FileSequenceSource(File file1) {
		setup(file1);
	}
		
	public void setup(File file1) {
		this.file1 = file1;
		this.file2 = null;
		reader = SequenceFormat.getReaderForFile(file1.getName());
	}

	
	public FileSequenceSource(String format,File file1) {
		setup(format,file1);
	}
	
	public void setup(String format,File file1) {
		this.file1 = file1;
		this.file2 = null;
		reader = SequenceFormat.getReaderForFile(file1.getName(),format);
		reader.setProgressMeter(pm);
	}
	

	
	public FileSequenceSource(final String filename) {
		setup(new File(filename));
	}
	
	public FileSequenceSource(final String format,final String filename1,final String filename2) {
		if (filename2 == null)
			setup(format,new File(filename1));
		else
			setup(format,new File(filename1),new File(filename2));
	}
	
	public FileSequenceSource(String format, String filename) {
		setup(format,new File(filename));
	}


	@Override
	public Iterator<Sequence> iterator() {
		reader.setProgressMeter(pm);
		try {
			if (file2 == null) {
				if (file1 == null)
					return reader.read(System.in);
				else
					return reader.read(CompressionUtils.openInputStream(file1));
			} else { 
				return reader.read(CompressionUtils.openInputStream(file1),
								   CompressionUtils.openInputStream(file2));
			}
		} catch(IOException e) {
			if (file2 == null)
				throw new FileProcessingRuntimeException(e,file1);
			else
				throw new MultipleFilesProcessingRuntimeException(e,file1,file2);				
		}
	}
}
