package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import bfx.Sequence;
import bfx.io.SequenceFormat;
import bfx.io.SequenceReader;
import bfx.utils.io.BaseSingleAndDualReader;

public class FastaSequenceReader extends BaseSingleAndDualReader<Iterator<Sequence>> implements SequenceReader {
	static {
		SequenceReader reader = new FastaSequenceReader();
		
		SequenceFormat.registerExtension("fasta", reader);
		SequenceFormat.registerExtension("fa", reader);
		SequenceFormat.registerExtension("csfasta", reader);
	}
	
	private byte defaultQuality = 0;
	
	public FastaSequenceReader() {};
	public FastaSequenceReader(byte defaultQuality) {
			this.defaultQuality = defaultQuality;
	};
	
	@Override
	public Iterator<Sequence> read(InputStream fastaInput) throws IOException {
		return new LineBasedFastaIterator(fastaInput,defaultQuality);
	}

	@Override
	public Iterator<Sequence> read(Reader fastaReader) throws IOException {
		return new LineBasedFastaIterator(fastaReader,defaultQuality);
	}

	@Override
	public Iterator<Sequence> read(InputStream fastaInput, InputStream qualInput)
			throws IOException {
		return new LineBasedFastaQualIterator(fastaInput,qualInput);
	}

	@Override
	public Iterator<Sequence> read(Reader fastaReader, Reader qualReader)
			throws IOException {
		return new LineBasedFastaQualIterator(fastaReader,qualReader);
	}

}