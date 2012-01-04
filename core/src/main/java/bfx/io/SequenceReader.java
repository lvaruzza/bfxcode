package bfx.io;

import java.util.Iterator;

import bfx.ProgressCounter;
import bfx.Sequence;
import bfx.utils.io.AbstractDualReader;
import bfx.utils.io.AbstractReader;
import bfx.utils.io.BaseSingleAndDualReader;


/**
 * Interface for SequenceReader
 *  
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public abstract class SequenceReader extends BaseSingleAndDualReader<Iterator<Sequence>> {

	protected ProgressCounter pc;
	
	/**
	 * Set ProgressCounter to be increment on each sequence.
	 * 
	 * @param pc ProgressCounter.
	 */
	public void setProgressCounter(ProgressCounter pc) {
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
