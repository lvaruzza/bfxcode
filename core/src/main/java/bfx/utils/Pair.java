package bfx.utils;

/**
 * Generic Pair of values of different types
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 * @param <F> Type of first value
 * @param <S> Type of second value
 */
public class Pair<F,S> {
	public F fst;
	public S snd;
	
	
	/**
	 * Construct a new pair
	 * 
	 * @param fst First value
	 * @param snd Second value
	 */
	public Pair(F fst,S snd) {
		this.fst = fst;
		this.snd = snd;
	}
}
