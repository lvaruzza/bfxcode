package bfx.utils.stat;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.annotation.Nullable;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;


public class TestMean {
	private static Logger log = LoggerFactory.getLogger(TestMean.class);

	Function<Long,Double> invX21k = new Function<Long,Double>() {

		@Override
		public Double apply(@Nullable Long i) {
			return 1000.0/(i*i);
		}
		
	};

	Function<Long,Double> sum = new Function<Long,Double>() {

		@Override
		public Double apply(@Nullable Long i) {
			return (double)i;
		}
		
	};
	
	public void testStat(UnivariableStat s,Iterable<Double> xs,double expected) {
		for(double x:xs) {
			s.add(x);
		}
		double m = s.get();
		log.info(String.format("Mean = %g, expected = %g (delta = %g)",m,expected,Math.abs((m-expected))));
		assertEquals(expected,m,1e-6);
	}

	public void meanBenchmark(Class<? extends UnivariableStat> klass)  {
		try {

			testStat(klass.newInstance(),Arrays.asList(-2.0,-1.0,1.0,2.0),0.0);
			testStat(klass.newInstance(),Arrays.asList(1.0,2.0,3.0),2.0);
			testStat(klass.newInstance(),NumericalSerie.make(sum, 1,1000), 1001.0/2.0);
			
			testStat(klass.newInstance(),NumericalSerie.make(invX21k, 1,1000), 1.6439345666815597);			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}		
	}
	
	@Test
	public void testTrivialMean() {
		meanBenchmark(TrivialMean.class);
	}
	
	@Test
	public void testOnlineMean() {
		meanBenchmark(OnlineMean.class);
	}
	
}
