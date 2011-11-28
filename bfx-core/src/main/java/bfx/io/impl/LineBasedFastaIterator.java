package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import bfx.Sequence;
import bfx.impl.SequenceConstQualImpl;
import bfx.utils.ByteBuffer;

public class LineBasedFastaIterator implements Iterator<Sequence> {
	//private static Logger log = Logger.getLogger(LineBasedFastaIterator.class);
	private LineIterator li;
	private byte defaultQuality;
	
	private ByteBuffer curseq;
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
				if (first) {
					first = false;
					header = line.substring(1);
					curseq = new ByteBuffer();
				} else {
					Sequence seq = new SequenceConstQualImpl(header,
														curseq.get(),
														defaultQuality);
					header = line.substring(1);
					curseq = new ByteBuffer();
					return seq;
				}
			} else {
				//System.out.println(line.trim());
				if (!first) curseq.append(line.trim().getBytes());
			}
		}
		return new SequenceConstQualImpl(header,
				curseq.get(),
				defaultQuality);
	}

	public void remove() {
		throw new RuntimeException("Invalid invocation of remove()");
	}

}
