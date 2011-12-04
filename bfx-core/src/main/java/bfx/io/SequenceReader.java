package bfx.io;

import java.util.Iterator;

import bfx.ProgressCounter;
import bfx.Sequence;
import bfx.utils.io.AbstractDualReader;
import bfx.utils.io.AbstractReader;


public interface SequenceReader extends AbstractReader<Iterator<Sequence>>,
										AbstractDualReader<Iterator<Sequence>> {

	public void setProgressCounter(ProgressCounter pc);		
	
	public String[] getPreferedExtensions();
	
	public String getFormatName();
}
