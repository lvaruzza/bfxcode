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
		ByteBuffer seq = new ByteBuffer();
		while(!line.startsWith("+")) {
			seq.append(line.getBytes());
			if (!li.hasNext()) throw new RuntimeException("Incomplete sequence in fastq stream.");
			line = li.next();
		}		
		int seqLen = seq.length();
		ByteBuffer qual = new ByteBuffer();
		do {
			if (!li.hasNext()) throw new RuntimeException("Incomplete sequence in fastq stream.");
			line = li.next();
			qual.append(line.getBytes());
		} while(qual.length()!=seqLen);
		return new SequenceQualImpl(header.substring(1), seq.get(), qualrepr.textToQual(qual.get()));
	}

	public void remove() {
		throw new RuntimeException("Invalid invocation of remove()");
	}

}
