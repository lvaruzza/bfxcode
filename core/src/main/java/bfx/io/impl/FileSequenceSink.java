package bfx.io.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.Sequence;
import bfx.io.SequenceFormat;
import bfx.io.SequenceSink;
import bfx.io.SequenceWriter;
import bfx.utils.compression.CompressionUtils;

/**
 * Sequence Sink based on a file
 * 
 * @author varuzza
 *
 */
public class FileSequenceSink extends SequenceSink {
	private Logger log = LoggerFactory.getLogger(FileSequenceSink.class);
	
	private File file1 = null;
	private File file2 = null;
	private OutputStream out1 = null;
	private OutputStream out2 = null;
	
	private SequenceWriter writer;
	
	private void setup(File file1,File file2) {
		setup(null,file1,file2);
	}

	private void setup(String format,File file1,File file2) {
		this.file1 = file1;
		this.file2 = file2;
		writer = SequenceFormat.getWriterForFile(file1.getName(),format);
		log.debug(String.format("New writer %s for files %s and %s",writer,file1,file2));
	}
	
	private void setup(File file1) {
		this.file1 = file1;
		this.file2 = null;
		writer = SequenceFormat.getWriterForFile(file1.getName());
		log.debug(String.format("New writer %s for file %s",writer,file1));
	}
	
	private void setup(String format,File file1) {
		setup(format,file1,null);
	}
	
	
	/**
	 * Create a new SequenceSink
	 * 
	 * @param file1 - First file
	 * @param file2 - Second file (normally the qual file)
	 */
	public FileSequenceSink(File file1,File file2) {
		setup(file1,file2);
	}
	
	/**
	 * Create a new SequenceSink
	 * 
	 * @param format - File format
	 * @param file1 - First file
	 * @param file2 - Second file (normally the qual file)
	 */
	public FileSequenceSink(String format,File file1,File file2) {
		setup(format,file1,file2);
	}
	
	/**
	 * Create a new SequenceSink
	 * 
	 * @param format - File format
	 * @param file1 - Output File
	 */
	public FileSequenceSink(File file1) {
		setup(file1);
	}
	
	/**
	 * Create a new SequenceSink
	 * 
	 * @param format - File format
	 * @param file1 - OUtput File
	 */
	public FileSequenceSink(String format,File file1) {
		setup(format,file1);
	}
	
	/**
	 * @param filename
	 */
	public FileSequenceSink(String filename) {
		setup(new File(filename));
	}

	/**
	 * Create a new SequenceSink
	 * 
	 * @param format - Output file format
	 * @param filename1 - First file 
	 * @param filename2  - Second file (normally qual file)
	 */
	public FileSequenceSink(String format,String filename1,String filename2) {
		if (filename2 == null)
			setup(format,new File(filename1));
		else
			setup(format,new File(filename1),new File(filename2));
	}

	/**
	 * Create a new SequenceSink
	 * 
	 * @param file1 - First file
	 * @param file2 - Second file (normally qual file)
	 */
	public FileSequenceSink(String file1, String file2) {
		setup(new File(file1),new File(file2));
	}

	private void openFiles() throws IOException {
		out1 = CompressionUtils.openOutputStream(file1);
		if (file2 != null)
			out2 = CompressionUtils.openOutputStream(file2);
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

	@Override
	public void close() throws IOException {
		if (out1!=null) out1.close();
		if (out2!=null) out2.close();
	}
}
