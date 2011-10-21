package biojava.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import biojava.impl.SequenceConstQualImpl;
import biolite.Sequence;

public class LineBasedFastaIterator implements Iterator<Sequence> {
	private LineIterator li;
	private byte defaultQuality;
	
	private StringBuilder curseq;
	private String line = "";
	private String header = "";
	private boolean first = false;
	
	public LineBasedFastaIterator(Reader fastaReader, byte defaultQuality) {
		li =  IOUtils.lineIterator(fastaReader);
		init(defaultQuality);
	}

	public LineBasedFastaIterator(InputStream fastaInput, byte defaultQuality) throws IOException {
		li =  IOUtils.lineIterator(fastaInput,"ASCII");
		init(defaultQuality);
	}

	private void init(byte defaultQuality) {
		this.defaultQuality = defaultQuality;		
	}
	
	public boolean hasNext() {
		return li.hasNext();
	}

	
	public Sequence next() {
		while(li.hasNext()) {
			line = li.next();
			if (line.startsWith(">")) {
				if (first) {
					first = false;
				} else {
					return new SequenceConstQualImpl(header,
														curseq.toString(),
														defaultQuality);
				}
				header = line;
				curseq = new StringBuilder();
			} else {
				curseq.append(line.trim());
			}
		}
		return new SequenceConstQualImpl(header,
				curseq.toString(),
				defaultQuality);
	}

	public void remove() {
		throw new RuntimeException("Invalid invocation of remove()");
	}

}
