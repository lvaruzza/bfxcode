package bfx.io;

import java.util.Iterator;

import bfx.GFF;

public  abstract class GFFSource implements Iterable<GFF> {

	@Override
	abstract public Iterator<GFF> iterator();
}
