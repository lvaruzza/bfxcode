package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import bfx.Sequence;
import bfx.io.SequenceFormat;
import bfx.io.SequenceReader;
import bfx.utils.io.BaseSingleAndDualReader;

public class FastQSequenceReader extends BaseSingleAndDualReader<Iterator<Sequence>> implements SequenceReader {
	static {
		SequenceReader reader = new FastQSequenceReader();
		
		SequenceFormat.registerExtension("fastq", reader);
		SequenceFormat.registerExtension("fq", reader);
		SequenceFormat.registerExtension("fastaq", reader);
	}
	
	
	public FastQSequenceReader() {};
	
	@Override
	public Iterator<Sequence> read(InputStream fastaInput) throws IOException {
		return new LineBasedFastQIterator(fastaInput);
	}

	@Override
	public Iterator<Sequence> read(Reader fastaReader) throws IOException {
		return new LineBasedFastQIterator(fastaReader);
	}

	@Override
	public Iterator<Sequence> read(InputStream fastaInput, InputStream qualInput)
			throws IOException {
		throw new RuntimeException("Not applicable for fastq file");
	}

	@Override
	public Iterator<Sequence> read(Reader fastaReader, Reader qualReader)
			throws IOException {
		throw new RuntimeException("Not applicable for fastq file");
	}

}
