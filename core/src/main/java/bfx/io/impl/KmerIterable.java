package bfx.io.impl;

import java.util.Arrays;
import java.util.Iterator;

import bfx.Sequence;
import bfx.io.SequenceSource;

public class KmerIterable implements Iterable<byte[]> {
	public static class KmerIterator implements Iterator<byte[]> {
		private KmerIterable iterable;
		private Iterator<Sequence> it;
		private Sequence curseq = null;
		private int pos;
		boolean hasMore = true;
		public KmerIterator(KmerIterable father) {
			this.iterable = father;
			it = father.src.iterator();
			pos = father.trimLeft;
			if (it.hasNext())
				curseq = it.next();
			else
				hasMore = false;
		}
		
		@Override
		public boolean hasNext() {
			return  hasMore;
		}

		private boolean endOfCurseq() {
			return (pos > curseq.length() - iterable.k -iterable.trimRight);
		}
		
		@Override
		public byte[] next() {
			byte[] r = Arrays.copyOfRange(curseq.getSeq(), pos, pos+iterable.k);
			pos++;
			
			if (endOfCurseq()) {
				if (it.hasNext()) {
					curseq = it.next();
					pos = iterable.trimLeft;
				} else
					hasMore = false;
			}
			return r;
		}
		
		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}
	protected SequenceSource src;
	protected int k;
	protected int trimLeft;
	protected int trimRight;
	
	public KmerIterable(SequenceSource src, int k,int trimLeft,int trimRight) {
		this.src = src;
		this.k = k;
		this.trimLeft = trimLeft;
		this.trimRight = trimRight;
	}

	@Override
	public Iterator<byte[]> iterator() {
		return new KmerIterator(this);
	}

}
