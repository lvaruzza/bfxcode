package bfx.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

import bfx.Sequence;
import bfx.exceptions.FileProcessingIOException;
import bfx.exceptions.MultipleFilesProcessingIOException;
import bfx.process.ProgressMeter;
import bfx.utils.io.BaseSingleAndDualWriter;


/**
 * SequenceWriter
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public abstract class SequenceWriter extends BaseSingleAndDualWriter<Iterator<Sequence>> {
	protected ProgressMeter pc;

	public void write(File file1, Sequence seq) throws IOException{
		try {
			OutputStream out = new FileOutputStream(file1);
			write(out,seq);
			out.close();
		} catch(IOException e) {
			throw new FileProcessingIOException(e,file1);
		}
	}
	
	public void write(File file1, File file2, Sequence seq) throws IOException{
		try {
			OutputStream out1 = new FileOutputStream(file1);
			OutputStream out2 = file2==null ? null : new FileOutputStream(file2); 
			write(out1,out2,seq);
			out1.close();
			if (out2!=null) out2.close();
		} catch(IOException e) {
			throw new MultipleFilesProcessingIOException(e,file1,file2);
		}
	}
	
	abstract public void write(OutputStream out1,Sequence seq) throws IOException;
	abstract public void write(OutputStream out1,OutputStream out2,Sequence seq) throws IOException;

	abstract public void write(Writer out1,Sequence seq) throws IOException;
	abstract public void write(Writer out1,Writer out2,Sequence seq) throws IOException;

	/**
	 * Set ProgressCounter to be increment on each sequence.
	 * 
	 * @param pc ProgressCounter.
	 */
	public void setProgressMeter(ProgressMeter pc) {
		this.pc = pc;
	}
	
	
	/**
	 * Return a list of life extensions associated with this SequenceWriter.
	 *   
	 * @return List of file extensions.
	 */
	abstract public String[] getPreferedExtensionsList();
	
	/**
	 * Name for this format
	 * 
	 * @return Format name
	 */
	abstract public String getFormatName();
	
	
	@Override
	public String toString() {
		return String.format("<SequenceWriter for %s format>",getFormatName());
	}

	public String getPreferedExtension() {
		return getPreferedExtensionsList()[0];
	}
}
