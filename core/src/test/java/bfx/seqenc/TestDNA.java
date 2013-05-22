package bfx.seqenc;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestDNA {

	@Test
	public void testGC1() {
		byte[] s = "CCCCGGGG".getBytes();
		double gc = DNA.GC(s,0,s.length);
		assertEquals(1.0,gc,0.01);
		System.out.println(gc);
		
		double gc2 = DNA.GC(s,0,s.length/2);
		assertEquals(1.0,gc2,0.01);
		System.out.println(gc2);

		double gc3 = DNA.GC(s,0,s.length/2+1);
		assertEquals(1.0,gc3,0.01);
		System.out.println(gc3);

		double gc4 = DNA.GC(s,2,s.length);
		assertEquals(1.0,gc4,0.01);
		System.out.println(String.format("%.2f",gc4));
		
	}
		
}
