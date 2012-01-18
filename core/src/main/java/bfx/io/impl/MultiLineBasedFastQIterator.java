package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import bfx.QualRepr;
import bfx.Sequence;
import bfx.exceptions.SequenceProcessingRuntimeException;
import bfx.impl.FastQRepr;
import bfx.impl.SequenceQual;
import bfx.process.ProgressCounter;
import bfx.utils.ByteBuffer;

public class MultiLineBasedFastQIterator implements Iterator<Sequence> {
	//private static Logger log = Logger.getLogger(LineBasedFastQIterator.class);
	private LineIterator li;
	private QualRepr qualrepr;
	private ProgressCounter pc;
	private long sequenceCount;
	
	public MultiLineBasedFastQIterator(Reader fastaReader,FastQRepr.FastqEncoding encoding,ProgressCounter pc) {
		li =  IOUtils.lineIterator(fastaReader);
		qualrepr = new FastQRepr(encoding);
		this.pc = pc;
		sequenceCount = 0;
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
		if (!li.hasNext()) throw new SequenceProcessingRuntimeException(sequenceCount,
				"Empty sequence in fastq stream.");
		String header = li.next();
		
		if (!header.startsWith("@")) throw new SequenceProcessingRuntimeException(sequenceCount,
				"Invalid fastQ sequence, header does not start with '@': " + header);
		
		
		if (!li.hasNext()) throw new SequenceProcessingRuntimeException(sequenceCount,
										"Header without sequence in fastq stream at " + header);
		String line = li.next();
		ByteBuffer seq = new ByteBuffer();
		while(!line.startsWith("+")) {
			seq.append(line.getBytes());
			if (!li.hasNext()) throw new SequenceProcessingRuntimeException(sequenceCount,
											"Sequence missing quality values in fastq stream at " + header);
			line = li.next();
		}		
		int seqLen = seq.length();
		ByteBuffer qual = new ByteBuffer();
		do {
			if (!li.hasNext()) throw new SequenceProcessingRuntimeException(sequenceCount,
												"Incomplete quality values in fastq stream at " + header);
			line = li.next();
			qual.append(line.getBytes());
			
	    // Color space sequences will have a difference of one position between sequence values and quality values
		// TODO: Find a better way to handle color encoded fastq files
		} while((seqLen - qual.length())  > 1);
		if (pc != null) pc.incr(1);
		sequenceCount++;
		return new SequenceQual(header.substring(1), seq.get(), qualrepr.textToQual(qual.get()));
	}

	public void remove() {
		throw new RuntimeException("Invalid invocation of remove()");
	}

}
