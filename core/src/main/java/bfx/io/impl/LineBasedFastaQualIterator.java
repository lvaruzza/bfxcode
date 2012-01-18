package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import bfx.QualRepr;
import bfx.Sequence;
import bfx.impl.FastaQualRepr;
import bfx.impl.SequenceQual;
import bfx.process.ProgressCounter;
import bfx.utils.ByteBuffer;

public class LineBasedFastaQualIterator implements Iterator<Sequence> {
	//private static Logger log = Logger.getLogger(LineBasedFastaQualIterator.class);
	private static QualRepr qualrepr = new FastaQualRepr();
	
	private LineIterator liseq;
	private LineIterator liqual;
	private ProgressCounter pc;
	
	private ByteBuffer curseq;
	private ByteBuffer curqual;
	private String seqline = "";
	private String qualline = "";
	private String header = "";
	private boolean first = true;
	
	public LineBasedFastaQualIterator(Reader fastaReader, Reader qualReader,ProgressCounter pc) {
		this.liseq = IOUtils.lineIterator(fastaReader);
		this.liqual = IOUtils.lineIterator(qualReader);
		this.pc = pc;
	}

	public LineBasedFastaQualIterator(InputStream fastaInput, InputStream qualInput,ProgressCounter pc) throws IOException {
		this.liseq = IOUtils.lineIterator(fastaInput,"ASCII");
		this.liqual = IOUtils.lineIterator(qualInput,"ASCII");	
		this.pc = pc;
	}

	public boolean hasNext() {
		return liseq.hasNext();
	}

	public Sequence next() {
		while(liseq.hasNext()) {
			seqline = liseq.next();
			if (seqline.startsWith(">")) {
				//log.debug(String.format("line=%s first=%s ",seqline,first));
				if (first) {
					first = false;
					header = seqline.substring(1);
					curseq = new ByteBuffer();
					//log.debug("Founded new sequence: '" + header +"'");
					// Go to the first sequence in the qual file
					while(liqual.hasNext()) {
						qualline = liqual.next();
						if(qualline.startsWith("#")) continue;
						//log.debug(String.format("1. qualline = %s'",qualline));
						if(qualline.startsWith(">"))break;
					}
				} else {
					//log.debug("Reading qual");
					curqual = new ByteBuffer();
					// Read the qual entry;
					assert(header.equals(qualline.substring(1)));
					
					while(liqual.hasNext()) {
						qualline = liqual.next();
						if(qualline.startsWith("#")) continue;
						if (qualline.startsWith(">")) break;
						curqual.append(qualline.getBytes());
						curqual.append(" ".getBytes());
					}
					
					//log.debug(String.format("Qual = '%s'",new String(curqual.get())));
					
					//log.debug("Returning sequence: '" + header +"'");
					Sequence seq = new SequenceQual(header,
														curseq.get(),
														qualrepr.textToQual(curqual.get()));
					header = seqline.substring(1);
					curseq = new ByteBuffer();
					curqual = new ByteBuffer();
					if (pc!=null) pc.incr(1);
					return seq;
				}
			} else {
				if (!first) curseq.append(seqline.trim().getBytes());
			}
		}
		curqual = new ByteBuffer();
		//log.debug(String.format("curqual = '%s'",curqual.get()));
		while(liqual.hasNext()) {
			qualline = liqual.next();
			if (qualline.startsWith(">")) break;
			curqual.append(qualline.getBytes());
			curqual.append(" ".getBytes());
		}
		if (pc!=null) pc.incr(1);
		return new SequenceQual(header,
				curseq.get(),
				qualrepr.textToQual(curqual.get()));
	}

	public void remove() {
		throw new RuntimeException("Invalid invocation of remove()");
	}

}
