package bfx.io;

import java.util.Iterator;

import bfx.Sequence;
import bfx.process.ProgressMeter;
import bfx.utils.io.BaseSingleAndDualReader;


/**
 * SequenceReader
 *  
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public abstract class SequenceReader extends BaseSingleAndDualReader<Iterator<Sequence>> {

	protected ProgressMeter pc;
	
	/**
	 * Set ProgressCounter to be increment on each sequence.
	 * 
	 * @param pc ProgressCounter.
	 */
	public void setProgressMeter(ProgressMeter pc) {
		this.pc = pc;
	}
	
	/**
	 * Return a list of life extensions associated with this SequenceReader.
	 *   
	 * @return List of file extensions.
	 */
	abstract public String[] getPreferedExtensions();
	
	
	/**
	 * Name for this format
	 * 
	 * @return format name
	 */
	abstract public String getFormatName();
}
