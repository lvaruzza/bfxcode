package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import bfx.Sequence;
import bfx.impl.SequenceConstQualImpl;

public class LineBasedFastaIterator implements Iterator<Sequence> {
	private static Logger log = Logger.getLogger(LineBasedFastaIterator.class);
	private LineIterator li;
	private byte defaultQuality;
	
	private StringBuilder curseq;
	private String line = "";
	private String header = "";
	private boolean first = true;
	
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
		//curseq = new StringBuilder();
	}
	
	public boolean hasNext() {
		return li.hasNext();
	}

	
	public Sequence next() {
		while(li.hasNext()) {
			line = li.next();
			if (line.startsWith(">")) {
				log.debug(String.format("line=%s first=%s ",line,first));
				if (first) {
					first = false;
					header = line.substring(1);
					curseq = new StringBuilder();
					log.debug("Founded new sequence: '" + header +"'");
				} else {
					log.debug("Returning sequence: '" + header +"'");
					Sequence seq = new SequenceConstQualImpl(header,
														curseq.toString(),
														defaultQuality);
					header = line.substring(1);
					curseq = new StringBuilder();
					return seq;
				}
			} else {
				if (!first) curseq.append(line.trim());
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
