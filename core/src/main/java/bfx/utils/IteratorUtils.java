package bfx.utils;

import java.util.Iterator;

public class IteratorUtils {
	public static long count(Iterator<?> it) {
		long c=0;
		while(it.hasNext()) {it.next(); c++; }
		return c;
	}
	
	public static <T> T first(Iterator<T> it) throws EmptyIteratorException {
		if(it.hasNext()) {
			return it.next();
		} else {
			throw new EmptyIteratorException();
		}
	}
}
