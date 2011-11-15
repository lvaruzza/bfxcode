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
import bfx.impl.SequenceQualImpl;

public class LineBasedFastaQualIterator implements Iterator<Sequence> {
	private static Logger log = Logger.getLogger(LineBasedFastaQualIterator.class);
	
	private LineIterator liseq;
	private LineIterator liqual;

	private StringBuilder curseq;
	private StringBuilder curqual;
	private String seqline = "";
	private String qualline = "";
	private String header = "";
	private boolean first = true;
	
	public LineBasedFastaQualIterator(Reader fastaReader, Reader qualReader) {
		this.liseq = IOUtils.lineIterator(fastaReader);
		this.liqual = IOUtils.lineIterator(qualReader);
	}

	public LineBasedFastaQualIterator(InputStream fastaInput, InputStream qualInput) throws IOException {
		this.liseq = IOUtils.lineIterator(fastaInput,"ASCII");
		this.liqual = IOUtils.lineIterator(qualInput,"ASCII");	
	}

	public boolean hasNext() {
		return liseq.hasNext();
	}

	public Sequence next() {
		while(liseq.hasNext()) {
			seqline = liseq.next();
			if (seqline.startsWith(">")) {
				log.debug(String.format("line=%s first=%s ",seqline,first));
				if (first) {
					first = false;
					header = seqline.substring(1);
					curseq = new StringBuilder();
					log.debug("Founded new sequence: '" + header +"'");
					// Go to the first sequence in the qual file
					while(liqual.hasNext()) {
						qualline = liqual.next();
						log.debug(String.format("1. qualline = %s'",qualline));
						if(qualline.startsWith(">"))break;
					}
				} else {
					log.debug("Reading qual");
					curqual = new StringBuilder();
					// Read the qual entry;
					assert(header.equals(qualline.substring(1)));
					
					while(liqual.hasNext()) {
						qualline = liqual.next();
						if (qualline.startsWith(">")) break;
						curqual.append(qualline);
						curqual.append(' ');
					}
					
					log.debug(String.format("Qual = '%s'",curqual.toString()));
					
					log.debug("Returning sequence: '" + header +"'");
					Sequence seq = new SequenceQualImpl(header,
														curseq.toString(),
														curqual.toString());
					header = seqline.substring(1);
					curseq = new StringBuilder();
					curqual = new StringBuilder();
					return seq;
				}
			} else {
				if (!first) curseq.append(seqline.trim());
			}
		}
		curqual = new StringBuilder();
		//log.debug(String.format("curqual = '%s'",curqual.toString()));
		while(liqual.hasNext()) {
			qualline = liqual.next();
			if (qualline.startsWith(">")) break;
			curqual.append(qualline);
			curqual.append(' ');
		}
		
		return new SequenceQualImpl(header,
				curseq.toString(),
				curqual.toString());
	}

	public void remove() {
		throw new RuntimeException("Invalid invocation of remove()");
	}

}
