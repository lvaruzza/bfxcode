package bfx.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bfx.io.SequenceSink;
import bfx.io.impl.FileSequenceSink;
import bfx.io.impl.StreamSequenceSink;
import bfx.process.ProgressMeterFactory;
import bfx.utils.compression.CompressionUtils;

import com.beust.jcommander.IVariableArity;
import com.beust.jcommander.Parameter;



/**
 * A Runnable Tool
 * 
 * @author varuzza
 *
 */
public  abstract class Tool {
	protected ProgressMeterFactory pcf;
	
	/**
	 * Run the Tool
	 * 
	 * @throws Exception
	 */
	public abstract void run() throws Exception;
	
	/**
	 * @return Tool name
	 */
	public abstract String getName();

	/**
	 * @return Tool Group
	 */
	public abstract String getGroup();
	
	
	/**
	 * Verbose Level
	 */
	@Parameter(names = {"--verbose","-v"}, description = "Verbose output")
	public boolean verbose = false;
	
	/**
	 * Return a FileOutputStrem from filename or stdout if filename is null or "-"
	 * If filename ends of a  compressed file extension it will return the proper 
	 * compressed stream.
	 * 
	 * @param filename
	 * @return a OutputStrem
	 * @throws IOException
	 */
	public OutputStream getStdOut(String filename) throws IOException {
		return CompressionUtils.fileOrStdOut(filename);
	}

	public OutputStream maybeOutput(String filename) throws IOException {
		return (filename == null) ? null : CompressionUtils.openOutputStream(filename);
	}
	
	public InputStream maybeInput(String filename) throws IOException {
		return (filename == null) ? null : CompressionUtils.openInputStream(filename);
	}
	
	protected SequenceSink getSequenceSink(String outputFormat, String output,
			String outputQual) {
		
		if (output == null) { 
			getProgressMeterFactory().disable();
			return new StreamSequenceSink(outputFormat);
		} else {
			return new FileSequenceSink(outputFormat, output, outputQual);
		}
	}


	
	/**
	 * Return a FileInputStrem from filename or stdin if filename is null or "-"
	 * If filename ends of a  compressed file extension it will return the proper 
	 * uncompressed stream.
	 * 
	 * @param filename
	 * @return an InputSteam
	 * @throws IOException
	 */
	public InputStream getStdIn(String filename) throws IOException {
		return CompressionUtils.fileOrStdIn(filename);
	}

	
	protected void execute() {
		try {
			run();
		} catch(Exception e) {
			System.err.println(String.format("Error execution tool '%': %s",
						getName(),e.getMessage()));
			
			e.printStackTrace(System.err);
		}
	}
	
	/**
	 * Set the progressMeterFactory
	 * 
	 * @param pcf
	 */
	public void setProgressMeterFactory(final ProgressMeterFactory pcf) {
		this.pcf = pcf;
	}
	
	/**
	 * @return a ProgressMeterFactory
	 */
	public ProgressMeterFactory getProgressMeterFactory() {
		return pcf;
	}

}
