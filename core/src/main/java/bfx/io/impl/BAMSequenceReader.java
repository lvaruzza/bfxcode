package bfx.io.impl;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamInputResource;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import bfx.Sequence;
import bfx.impl.SequenceQual;
import bfx.io.SequenceReader;

public class BAMSequenceReader extends SequenceReader {
	public static class BAMIterator implements Iterator<Sequence> {
		private Iterator<SAMRecord> samit;
		private SamReader reader;
		
		public BAMIterator(InputStream input) {
			reader = SamReaderFactory.makeDefault().open(SamInputResource.of(input));					
			samit = reader.iterator();
		}
		
		@Override
		public boolean hasNext() {
			if (!samit.hasNext())
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			
			return samit.hasNext();
		}

		@Override
		public Sequence next() {
			SAMRecord align = samit.next();
			return new SequenceQual(align.getReadName(), 
					align.getReadBases(),
					align.getBaseQualities());
		}

		@Override
		public void remove() {
			throw new RuntimeException("Unimplemented");
		}
		
	}
	
	@Override
	public String getFormatName() {
		return "BAM";
	}

	@Override
	public Iterator<Sequence> read(InputStream in) throws IOException {
		return new BAMIterator(in);
	}

	@Override
	public Iterator<Sequence> read(Reader in) throws IOException {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public Iterator<Sequence> read(InputStream input1, InputStream input2)
			throws IOException {
		return new BAMIterator(input1);
	}

	@Override
	public Iterator<Sequence> read(Reader reader1, Reader reader2)
			throws IOException {
		throw new RuntimeException("Unimplemented");
	}

}
