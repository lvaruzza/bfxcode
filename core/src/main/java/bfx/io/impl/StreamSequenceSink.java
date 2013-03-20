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

/**
 * Sequence Sink based on a file
 * 
 * @author varuzza
 *
 */
public class StreamSequenceSink extends SequenceSink {
	private Logger log = LoggerFactory.getLogger(StreamSequenceSink.class);
	
	private OutputStream out1 = null;
	private OutputStream out2 = null;
	
	private SequenceWriter writer;
	
	/**
	 * Create a new SequenceSink
	 * 
	 * @param file1 - First file
	 * @param file2 - Second file (normally the qual file)
	 */
	public StreamSequenceSink(String format,OutputStream out1,OutputStream out2) {
		this.out1 = out1;
		this.out2 = out2;
		writer = SequenceFormat.getWriter(format);
		log.debug(String.format("New writer %s",writer));
	}
	
	
	
	/**
	 * Create a new SequenceSink for stdout
	 * 
	 * @param format - File format
	 * @param out - OUtput Strem
	 */
	public StreamSequenceSink(String format) {
		this.out1 = System.out;
		this.out2 = null;
		writer = SequenceFormat.getWriter(format);
		log.debug(String.format("New writer %s for STDOUT",writer));
	}

	/**
	 * Create a new SequenceSink
	 * 
	 * @param format - File format
	 * @param file1 - OUtput File
	 */
	public StreamSequenceSink(String format,OutputStream out1) {
		this.out1 = out1;
		this.out2 = null;
		writer = SequenceFormat.getWriter(format);
		log.debug(String.format("New writer %s",writer));
	}
	

	@Override
	public void write(Sequence seq) throws IOException {
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
