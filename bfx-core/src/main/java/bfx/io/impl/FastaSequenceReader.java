package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import bfx.ProgressCounter;
import bfx.Sequence;
import bfx.io.SequenceReader;
import bfx.utils.io.BaseSingleAndDualReader;

public class FastaSequenceReader extends BaseSingleAndDualReader<Iterator<Sequence>> implements SequenceReader {
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
		if (qualInput != null)
			return new LineBasedFastaQualIterator(fastaInput,qualInput);
		else
			return read(fastaInput);
	}

	@Override
	public Iterator<Sequence> read(Reader fastaReader, Reader qualReader)
			throws IOException {
		if (qualReader != null)
			return new LineBasedFastaQualIterator(fastaReader,qualReader);
		else
			return read(fastaReader);
	}
	

	public static String[] fastaExtensions = {"fasta","fa","csfasta"};
	@Override
	public String[] getPreferedExtensions() {
		return fastaExtensions;
	}

	private ProgressCounter pc;
	
	@Override
	public void setProgressCounter(ProgressCounter pc) {
		this.pc = pc;
	}
	
	@Override
	public String getFormatName() {
		return "fasta";
	}
}
