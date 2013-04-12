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


	@Override
	public String toString() {
		return "Pair [fst=" + fst + ", snd=" + snd + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fst == null) ? 0 : fst.hashCode());
		result = prime * result + ((snd == null) ? 0 : snd.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (fst == null) {
			if (other.fst != null)
				return false;
		} else if (!fst.equals(other.fst))
			return false;
		if (snd == null) {
			if (other.snd != null)
				return false;
		} else if (!snd.equals(other.snd))
			return false;
		return true;
	}
	
	
}
