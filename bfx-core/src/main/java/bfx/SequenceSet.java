package bfx;

import java.util.Iterator;

import bfx.utils.IteratorUtils;

public abstract class SequenceSet implements Iterable<Sequence> {

	@Override
	abstract public Iterator<Sequence> iterator();
	
	public long count() {
		return IteratorUtils.count(iterator());
	}
}
