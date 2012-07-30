package bfx.utils.stat;

import java.util.Collection;

/**
 * Any Univariate Statistc
 * 
 * @author varuzza
 *
 */
public abstract class UnivariableStat {
	public abstract void add(double x);
	public void add(Collection<Number> xs) {
		for(Number x:xs) {
			add(x.doubleValue());
		}
	}
	public void add(double... xs) {
		for(double x:xs) {
			add(x);
		}
	}
	public abstract double get();
}
