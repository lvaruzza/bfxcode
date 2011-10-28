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

public class LineBasedFastQIterator implements Iterator<Sequence> {
	private static Logger log = Logger.getLogger(LineBasedFastQIterator.class);
	private LineIterator li;
	private byte defaultQuality;
	
	private StringBuilder curseq;
	private String line = "";
	private String header = "";
	private boolean first = true;
	
	public LineBasedFastQIterator(Reader fastaReader) {
		li =  IOUtils.lineIterator(fastaReader);
	}

	public LineBasedFastQIterator(InputStream fastaInput) throws IOException {
		li =  IOUtils.lineIterator(fastaInput,"ASCII");
	}

	
	public boolean hasNext() {
		return li.hasNext();
	}

	
	public Sequence next() {
		throw new RuntimeException("Not implemented yeat!!!");
	}

	public void remove() {
		throw new RuntimeException("Invalid invocation of remove()");
	}

}
