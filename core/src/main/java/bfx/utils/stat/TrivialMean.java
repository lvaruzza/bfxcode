package bfx.utils.stat;

/**
 * Trivial Algorithm for calculating Mean
 * 
 * @author varuzza
 *
 */
public class TrivialMean extends UnivariableStat {
	double sum = 0.0;
	double count = 0.0;
	
	@Override
	public void add(double x) {
		sum += x;
		count++;

	}

	@Override
	public double get() {
		return sum/count;
	}

}

