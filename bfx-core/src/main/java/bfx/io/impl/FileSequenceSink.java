package bfx.io.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import bfx.Sequence;
import bfx.SequenceSink;
import bfx.io.SequenceFormats;
import bfx.io.SequenceWriter;

public class FileSequenceSink extends SequenceSink {

	private File file1 = null;
	private File file2 = null;
	private FileOutputStream out1 = null;
	private FileOutputStream out2 = null;
	
	private SequenceWriter writer;
	
	public FileSequenceSink(File file1,File file2) {
		this.file1 = file1;
		this.file2 = file2;
		writer = SequenceFormats.getWriter(file1.getName());
	}

	public FileSequenceSink(String format,File file1,File file2) {
		this.file1 = file1;
		this.file2 = file2;
		writer = SequenceFormats.getWriter(file1.getName(),format);
	}
	
	public FileSequenceSink(File file1) {
		this.file1 = file1;
		this.file2 = null;
		writer = SequenceFormats.getWriter(file1.getName());
	}

	
	public FileSequenceSink(String format,File file1) {
		this.file1 = file1;
		this.file2 = null;
		writer = SequenceFormats.getWriter(file1.getName(),format);
	}
	
	public FileSequenceSink(String filename) {
		this(new File(filename));
	}

	public FileSequenceSink(String filename1,String filename2) {
		this(new File(filename1),new File(filename2));
	}

	private void openFiles() throws FileNotFoundException {
		out1 = new FileOutputStream(file1);
		if (file2 != null)
			out2 = new FileOutputStream(file2);
	}
	@Override
	public void write(Sequence seq) throws IOException {
		if (out1 == null)
			openFiles();
		
		if (out2==null)
			writer.write(out1,seq);
		else
			writer.write(out1,out2,seq);
	}
}
