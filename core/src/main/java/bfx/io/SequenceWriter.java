package bfx.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

import bfx.ProgressCounter;
import bfx.Sequence;
import bfx.utils.io.AbstractDualWriter;
import bfx.utils.io.AbstractWriter;


/**
 * Interface for Writing Sequences
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public interface SequenceWriter extends AbstractWriter<Iterator<Sequence>>,AbstractDualWriter<Iterator<Sequence>> {

	public void write(File file1,Sequence seq) throws IOException;
	public void write(File file1,File file2,Sequence seq) throws IOException;
	public void write(OutputStream out1,Sequence seq) throws IOException;
	public void write(OutputStream out1,OutputStream out2,Sequence seq) throws IOException;

	public void write(Writer out1,Sequence seq) throws IOException;
	public void write(Writer out1,Writer out2,Sequence seq) throws IOException;

	/**
	 * Set ProgressCounter to be increment on each sequence.
	 * 
	 * @param pc ProgressCounter.
	 */
	public void setProgressCounter(ProgressCounter pc);
	
	/**
	 * 
	 * @return
	 */
	public String[] getPreferedExtensions();
	
	/**
	 * Name for this format
	 * 
	 * @return Format name
	 */
	public String getFormatName();
}
