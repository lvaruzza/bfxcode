package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.Iterator;

import bfx.Sequence;
import bfx.io.SequenceReader;

public class SerializableSequenceReader extends SequenceReader {

	public static class ObjectIterator implements Iterator<Sequence>{
		private ObjectInputStream in;
		private boolean finished = false;
		private Sequence ret = null;
		
		protected ObjectIterator(ObjectInputStream in) {
			this.in = in;
		}
		
		@Override
		public boolean hasNext() {
			try {
				ret = (Sequence)in.readObject();
				if (ret == null) {
					return false;
				} else {
					return true;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Sequence next() {
			return ret;
		}

		@Override
		public void remove() {
			throw new RuntimeException("Unimplemented");
		}
		
	}
	
	@Override
	public String getFormatName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Sequence> read(InputStream in) throws IOException {
		return new ObjectIterator(new ObjectInputStream(in));
	}

	@Override
	public Iterator<Sequence> read(Reader in) throws IOException {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public Iterator<Sequence> read(InputStream input1, InputStream input2)
			throws IOException {
		return read(input1);
	}

	@Override
	public Iterator<Sequence> read(Reader reader1, Reader reader2)
			throws IOException {
		return read(reader1);
	}
}
