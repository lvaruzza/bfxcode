package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import bfx.Sequence;
import bfx.io.SequenceFormat;
import bfx.io.SequenceReader;
import bfx.io.SequenceSource;

public class StreamSequenceSource extends SequenceSource {

	private SequenceReader reader;

	private InputStream in1;
	private InputStream in2;
	

	public StreamSequenceSource(String format,InputStream in1,InputStream in2) {
		setup(format,in1,in2);
	}
	
	public void setup(String format,InputStream in1,InputStream in2) {
		this.in1 = in1;
		this.in2 = in2;
		reader = SequenceFormat.getReader(format);
		reader.setProgressMeter(pm);
	}
	
		
	
	public StreamSequenceSource(String format,InputStream in1) {
		setup(format,in1,null);
	}

	@Override
	public Iterator<Sequence> iterator() {
		reader.setProgressMeter(pm);
		try {
			if (in2 == null) {
				if (in1 == null)
					return reader.read(System.in);
				else
					return reader.read(in1);
			} else { 
				return reader.read(in1,in2);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
