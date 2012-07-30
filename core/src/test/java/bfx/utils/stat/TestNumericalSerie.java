package bfx.utils.stat;

import javax.annotation.Nullable;

import org.junit.Test;

import com.google.common.base.Function;

public class TestNumericalSerie {

	@Test
	public void testInverX2() {
		Function<Long,Double> fun = new Function<Long,Double>() {

			@Override
			public Double apply(@Nullable Long i) {
				return 1.0/(i*i);
			}
			
		};
		
		System.out.println(NumericalSerie.make(fun,1,10).sum()/10.0);
		System.out.println(NumericalSerie.make(fun,1,100).sum()/100.0);
		double m1k = NumericalSerie.make(fun,1,1000000).sum()/1e6;
		
		System.out.println(m1k);
		System.out.println(Math.abs(m1k-Math.PI*Math.PI/6e6));
	}
}
