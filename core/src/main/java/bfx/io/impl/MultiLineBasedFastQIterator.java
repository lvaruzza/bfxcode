package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import bfx.ProgressCounter;
import bfx.QualRepr;
import bfx.Sequence;
import bfx.impl.FastQRepr;
import bfx.impl.SequenceQual;
import bfx.utils.ByteBuffer;

public class MultiLineBasedFastQIterator implements Iterator<Sequence> {
	//private static Logger log = Logger.getLogger(LineBasedFastQIterator.class);
	private LineIterator li;
	private QualRepr qualrepr;
	private ProgressCounter pc;
	
	public MultiLineBasedFastQIterator(Reader fastaReader,FastQRepr.FastqEncoding encoding,ProgressCounter pc) {
		li =  IOUtils.lineIterator(fastaReader);
		qualrepr = new FastQRepr(encoding);
		this.pc = pc;
	}

	public MultiLineBasedFastQIterator(InputStream fastaInput,FastQRepr.FastqEncoding encoding,ProgressCounter pc) throws IOException {
		li =  IOUtils.lineIterator(fastaInput,"ASCII");
		qualrepr = new FastQRepr(encoding);
		this.pc = pc;
	}

	
	public boolean hasNext() {
		return li.hasNext();
	}
	
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
		if (pc != null) pc.incr(1);
		return new SequenceQual(header.substring(1), seq.get(), qualrepr.textToQual(qual.get()));
	}

	public void remove() {
		throw new RuntimeException("Invalid invocation of remove()");
	}

}
