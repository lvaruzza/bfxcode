package bfx.io.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bfx.impl.FastaQualRepr;

public class TestFastaQualRepr {
	//private static Logger log = Logger.getLogger(TestFastaQualRepr.class);
	private static FastaQualRepr repr = new FastaQualRepr();
	
	private void testStringToQual0(String in) {
		byte[] b = repr.textToQual(in.getBytes());
		for (byte i=0;i<b.length;i++) {
			assertEquals(i,b[i]);
		}
	}
	
	@Test
	public void testStringToQual() {
		testStringToQual0("0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20");
		testStringToQual0("   0 1 2 3 4 5 6 7\n 8 9 10 11 12 13 14 15 16 17 18 19 20");
		testStringToQual0("0 1 2 3 4 5 6 7 8              9 10 11 12 13 14 15 16 17 18 19 20        ");
	}
	
	@Test
	public void testQualToString() {
		String r = repr.qualToTextString(new byte[]{1,2,3,4});
		assertEquals("1 2 3 4",r);
	}	
}
