package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.Sequence;
import bfx.impl.SequenceConstQual;
import bfx.process.ProgressMeter;
import bfx.utils.ByteBuffer;

public class LineBasedFastaIterator implements Iterator<Sequence> {
	private static Logger log = LoggerFactory.getLogger(LineBasedFastaIterator.class);
	private LineIterator li;
	private byte defaultQuality;
	private ProgressMeter pc;
	
	private ByteBuffer curseq;
	private String line = "";
	private String header = "";
	private boolean first = true;
	
	public LineBasedFastaIterator(Reader fastaReader, byte defaultQuality,ProgressMeter pc) {
		li =  IOUtils.lineIterator(fastaReader);
		this.defaultQuality = defaultQuality;
		this.pc = pc;
		log.info("Progress Counter = " + pc);
	}

	public LineBasedFastaIterator(InputStream fastaInput, byte defaultQuality,ProgressMeter pc) throws IOException {
		li =  IOUtils.lineIterator(fastaInput,"ASCII");
		this.defaultQuality = defaultQuality;
		this.pc = pc;
		log.info("Progress Counter = " + pc);
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
					
					Sequence seq = new SequenceConstQual(header,
														curseq.get(),
														defaultQuality);
					header = line.substring(1);
					curseq = new ByteBuffer();
					if (pc!=null) pc.incr(1);
					return seq;
				}
			} else {
				//System.out.println(line.trim());
				if (!first) curseq.append(line.trim().getBytes());
			}
		}
		if (pc!=null) pc.incr(1);
		return new SequenceConstQual(header,
				curseq.get(),
				defaultQuality);
	}

	public void remove() {
		throw new RuntimeException("Invalid invocation of remove()");
	}

}
