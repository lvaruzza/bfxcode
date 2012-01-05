package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import bfx.Sequence;
import bfx.impl.FastQRepr;
import bfx.impl.FastQRepr.FastqEncoding;
import bfx.io.SequenceReader;

public class FastQSequenceReader extends SequenceReader {
	private FastQRepr.FastqEncoding encoding;
	private boolean useSingleLineReader;
	
	public FastQSequenceReader(FastQRepr.FastqEncoding qualEncoding,boolean useSingleLineReader) {
		this.encoding = qualEncoding;
		this.useSingleLineReader = useSingleLineReader;
	};

	public FastQSequenceReader() {
		this(FastqEncoding.SANGER,false);
	};
	
	public void setEncoding(FastQRepr.FastqEncoding qualEncoding) {
		this.encoding = qualEncoding;
	}
	
	public void fastqSequenceSingleLine(boolean singleLine) {
		useSingleLineReader = singleLine;
	}
	
	@Override
	public Iterator<Sequence> read(InputStream fastaInput) throws IOException {
		if (useSingleLineReader)
			return new LineBasedFastQIterator(fastaInput,encoding);
		else
			return new MultiLineBasedFastQIterator(fastaInput,encoding);
	}

	@Override
	public Iterator<Sequence> read(Reader fastaReader) throws IOException {
		if (useSingleLineReader)
			return new LineBasedFastQIterator(fastaReader,encoding);
		else
			return new MultiLineBasedFastQIterator(fastaReader,encoding);
	}

	@Override
	public Iterator<Sequence> read(InputStream fastqInput, InputStream ignored)
			throws IOException {
		return read(fastqInput);
	}

	@Override
	public Iterator<Sequence> read(Reader fastqReader, Reader ignored)
			throws IOException {
		return read(fastqReader);
	}

	public static String[] fastQExtensions = {"fastq","fq","csfastq"};

	@Override
	public String[] getPreferedExtensions() {
		return fastQExtensions;
	}
	
	@Override
	public String getFormatName() {
		return "fastQ";
	}
	
}
