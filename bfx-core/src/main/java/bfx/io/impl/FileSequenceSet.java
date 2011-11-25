package bfx.io.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import bfx.Sequence;
import bfx.SequenceSet;
import bfx.exceptions.FileProcessingRuntimeException;
import bfx.io.SequenceFormats;
import bfx.io.SequenceReader;

public class FileSequenceSet extends SequenceSet {

	private File file;
	private SequenceReader reader;
	
	public FileSequenceSet(File file) {
		this.file = file;
		reader = SequenceFormats.getReader(file.getName());
	}

	public FileSequenceSet(String filename) {
		this(new File(filename));
	}
	
	@Override
	public Iterator<Sequence> iterator() {
		try {
			return reader.read(file);
		} catch(IOException e) {
			throw new FileProcessingRuntimeException(e,file);
		}
	}

}
