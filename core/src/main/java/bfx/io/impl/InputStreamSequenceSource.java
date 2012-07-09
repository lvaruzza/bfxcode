package bfx.io.impl;

import java.io.InputStream;
import java.util.Iterator;

import bfx.Sequence;
import bfx.exceptions.IterableAlreadyUsed;
import bfx.io.SequenceFormats;
import bfx.io.SequenceReader;
import bfx.io.SequenceSource;

/**
 * Sequence Source from a InputStream
 * 
 * Can only be used once.
 * 
 * @author varuzza
 *
 */
public class InputStreamSequenceSource extends SequenceSource {
	private SequenceReader reader;
	private boolean used = false;
	private InputStream input;

	public InputStreamSequenceSource(final String format) {
		reader = SequenceFormats.getReader(format);
		input = System.in;
	}
	
	public InputStreamSequenceSource(final String format,final InputStream input) {
		reader = SequenceFormats.getReader(format);
		this.input = input;
	}
	
	@Override
	public Iterator<Sequence> iterator() {
		if (used == false) {
			used=true;
			try {
				return reader.read(input);
			} catch(Exception e) {
				throw new RuntimeException("Error in Iterable",e);
			}
		} else 
			throw new IterableAlreadyUsed();
		
	}

}
