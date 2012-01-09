package bfx.io.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import bfx.Sequence;
import bfx.io.SequenceFormats;
import bfx.io.SequenceReader;
import bfx.io.SequenceSource;

public class BufferSequenceSource extends SequenceSource {

	private SequenceReader reader;
	private byte[] buffer;
	
	public BufferSequenceSource(String formatName,byte[] buffer) {
		this.buffer=buffer;
		this.reader=SequenceFormats.getReader(formatName);
	}
	
	@Override
	public Iterator<Sequence> iterator()  {
		try {
			return reader.read(new ByteArrayInputStream(buffer));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
