package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import bfx.Sequence;
import bfx.impl.FastQRepr;
import bfx.impl.FastQRepr.FastqEncoding;
import bfx.io.SequenceReader;
import bfx.utils.io.BaseSingleAndDualReader;

public class FastQSequenceReader extends BaseSingleAndDualReader<Iterator<Sequence>> implements SequenceReader {
	private FastQRepr.FastqEncoding encoding;

	public FastQSequenceReader(FastQRepr.FastqEncoding qualEncoding) {
		this.encoding=qualEncoding;
	};

	public FastQSequenceReader() {
		this(FastqEncoding.SANGER);
	};
	
	public void setEncoding(FastQRepr.FastqEncoding qualEncoding) {
		this.encoding = qualEncoding;
	}
	
	@Override
	public Iterator<Sequence> read(InputStream fastaInput) throws IOException {
		return new LineBasedFastQIterator(fastaInput,encoding);
	}

	@Override
	public Iterator<Sequence> read(Reader fastaReader) throws IOException {
		return new LineBasedFastQIterator(fastaReader,encoding);
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

	public static String[] fastQExtensions = {"fastq","fq","csfastq"};

	@Override
	public String[] getPreferedExtensions() {
		return fastQExtensions;
	}

	
}
