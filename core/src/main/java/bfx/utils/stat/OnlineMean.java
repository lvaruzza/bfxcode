package bfx.utils.stat;

/**
 * Online Algorithm for calculating Mean
 * 
 * @author varuzza
 *
 */
public class OnlineMean extends UnivariableStat {
	double mean = 0.0;
	double n = 0.0;
	
	@Override
	public void add(double x) {
		n++;
		double delta = x - mean;
		mean = mean + delta/n;
	}

	@Override
	public double get() {
		return mean;
	}

}
