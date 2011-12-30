package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import bfx.QualRepr;
import bfx.Sequence;
import bfx.impl.FastQRepr;
import bfx.impl.SequenceQualImpl;
import bfx.utils.ByteBuffer;

public class MultiLineBasedFastQIterator implements Iterator<Sequence> {
	//private static Logger log = Logger.getLogger(LineBasedFastQIterator.class);
	private LineIterator li;
	private QualRepr qualrepr;
	
	public MultiLineBasedFastQIterator(Reader fastaReader,FastQRepr.FastqEncoding encoding) {
		li =  IOUtils.lineIterator(fastaReader);
		qualrepr = new FastQRepr(encoding);
	}

	public MultiLineBasedFastQIterator(InputStream fastaInput,FastQRepr.FastqEncoding encoding) throws IOException {
		li =  IOUtils.lineIterator(fastaInput,"ASCII");
		qualrepr = new FastQRepr(encoding);
	}

	
	public boolean hasNext() {
		return li.hasNext();
	}
	
	//TODO
	public Sequence next() {
		if (!li.hasNext()) throw new RuntimeException("Incomplete sequence in fastq stream.");
		String header = li.next();
		if (!header.startsWith("@")) throw new RuntimeException("Invalid fastQ sequence, header does not start with '@': " + header);
		if (!li.hasNext()) throw new RuntimeException("Incomplete sequence in fastq stream.");
		String line = li.next();
		ByteBuffer curseq = new ByteBuffer();
		while(!line.startsWith("+")) {
			curseq.append(line.getBytes());
			if (!li.hasNext()) throw new RuntimeException("Incomplete sequence in fastq stream.");
			line = li.next();
		}		
		if (!li.hasNext()) throw new RuntimeException("Incomplete sequence in fastq stream.");
		String sep = li.next();
		if (!sep.startsWith("+")) throw new RuntimeException("Invalid fastQ sequence, quality separator does not start with '+': " + sep);
		if (!li.hasNext()) throw new RuntimeException("Incomplete sequence in fastq stream.");
		String qual = li.next();
		return new SequenceQualImpl(header.substring(1), seq.getBytes(), qualrepr.textToQual(qual));
	}

	public void remove() {
		throw new RuntimeException("Invalid invocation of remove()");
	}

}
