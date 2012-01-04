package bfx.io;

import java.util.Iterator;

import bfx.ProgressCounter;
import bfx.Sequence;
import bfx.utils.io.AbstractDualReader;
import bfx.utils.io.AbstractReader;


/**
 * Interface for SequenceReader
 *  
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public interface SequenceReader extends AbstractReader<Iterator<Sequence>>,
										AbstractDualReader<Iterator<Sequence>> {


	/**
	 * Set ProgressCounter to be increment on each sequence.
	 * 
	 * @param pc ProgressCounter.
	 */
	public void setProgressCounter(ProgressCounter pc);		
	
	/**
	 * Return a list of life extensions associated with this SequenceReader.
	 *   
	 * @return List of file extensions.
	 */
	public String[] getPreferedExtensions();
	
	
	/**
	 * Name for this format
	 * 
	 * @return format name
	 */
	public String getFormatName();
}
