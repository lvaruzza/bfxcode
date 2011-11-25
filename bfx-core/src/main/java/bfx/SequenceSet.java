package bfx;

import java.util.Iterator;

public abstract class SequenceSet implements Iterable<Sequence> {

	@Override
	abstract public Iterator<Sequence> iterator();
	
	
	
}
