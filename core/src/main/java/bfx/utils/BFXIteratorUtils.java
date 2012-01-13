package bfx.utils;

import java.util.Iterator;

import bfx.exceptions.EmptyIteratorException;

/**
 * Iterator Utils
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class BFXIteratorUtils {
	
	
	/**
	 * Count number of elements in iterator
	 * 
	 * @param it Iterator
	 * @return Number of elements
	 */
	public static long count(Iterator<?> it) {
		long c=0;
		while(it.hasNext()) {it.next(); c++; }
		return c;
	}
	
	/**
	 * Return the first element of iterator.
	 * 
	 * @param it Itertor
	 * @return First element of iterator
	 * @throws EmptyIteratorException
	 */
	public static <T> T first(Iterator<T> it) throws EmptyIteratorException {
		if(it.hasNext()) {
			return it.next();
		} else {
			throw new EmptyIteratorException();
		}
	}
}
