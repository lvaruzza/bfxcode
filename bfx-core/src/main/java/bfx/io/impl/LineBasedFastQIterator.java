package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import bfx.QualRepr;
import bfx.Sequence;
import bfx.impl.FastQRepr;
import bfx.impl.SequenceQualImpl;

public class LineBasedFastQIterator implements Iterator<Sequence> {
	private static Logger log = Logger.getLogger(LineBasedFastQIterator.class);
	private LineIterator li;
	private QualRepr qualrepr;
	
	public LineBasedFastQIterator(Reader fastaReader,FastQRepr.FastqEncoding encoding) {
		li =  IOUtils.lineIterator(fastaReader);
		qualrepr = new FastQRepr(encoding);
	}

	public LineBasedFastQIterator(InputStream fastaInput,FastQRepr.FastqEncoding encoding) throws IOException {
		li =  IOUtils.lineIterator(fastaInput,"ASCII");
		qualrepr = new FastQRepr(encoding);
	}

	
	public boolean hasNext() {
		return li.hasNext();
	}

	
	public Sequence next() {
		if (!li.hasNext()) throw new RuntimeException("Incomplete sequence in fastq stream.");
		String header = li.next();
		if (!header.startsWith("@")) throw new RuntimeException("Invalid fastQ sequence, header does not start with '@': " + header);
		if (!li.hasNext()) throw new RuntimeException("Incomplete sequence in fastq stream.");
		String seq = li.next();
		// throw separator
		if (!li.hasNext()) throw new RuntimeException("Incomplete sequence in fastq stream.");
		String sep = li.next();
		if (!header.startsWith("+")) throw new RuntimeException("Invalid fastQ sequence, quality separator does not start with '+': " + sep);
		if (!li.hasNext()) throw new RuntimeException("Incomplete sequence in fastq stream.");
		String qual = li.next();
		return new SequenceQualImpl(header.substring(1), seq.getBytes(), qualrepr.textToQual(qual));
	}

	public void remove() {
		throw new RuntimeException("Invalid invocation of remove()");
	}

}
