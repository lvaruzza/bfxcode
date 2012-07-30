package bfx.utils.stat;

import java.util.Iterator;

import com.google.common.base.Function;


/**
 * Implement a numerical serie x_i
 * 
 * @author varuzza
 *
 */
public class NumericalSerie implements Iterable<Double> {
	private long _start;
	private long _end;
	private Function<Long,Double> _fun;
	
	public NumericalSerie(Function<Long,Double> f,long end) {
		this(f,1,end);
	}

	public NumericalSerie(Function<Long,Double> f,long start,long end) {
		this._end = end;
		this._start = start;
		this._fun = f;
	}
		
	/**
	 * @return Sum from start to end of x_i
	 */
	public double sum() {
		double sum = 0.0;
		for(double x:this) {
			sum += x;
		}
		return sum;
	}
	
	@Override
	public Iterator<Double> iterator() {
		final long start = _start;
		final long end = _end;
		final Function<Long,Double> fun = _fun;
		
		return new Iterator<Double>() {
			private long i = start;
			
			@Override
			public boolean hasNext() {
				return (i<=end);
			}

			@Override
			public Double next() {
				return fun.apply(i++);
			}

			@Override
			public void remove() {
				throw new RuntimeException("Not implemented");
			}
			
		};
	}

	/**
	 * Utility function to create a new Serie
	 * 
	 * @param fun - Function
	 * @param start - Start value
	 * @param end - end value
	 * @return
	 */
	public static NumericalSerie make(final Function<Long, Double> fun, final int start, final int end) {
		return new NumericalSerie(fun,start,end);
	}

	/**
	 * Utility function to create a new Serie staring in 1
	 * 
	 * @param fun - Function
	 * @param end - end value
	 * @return
	 */
	public static NumericalSerie make(final Function<Long, Double> fun, final int end) {
		return new NumericalSerie(fun,end);
	}	
}
