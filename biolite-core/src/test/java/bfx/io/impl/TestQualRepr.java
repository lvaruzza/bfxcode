package bfx.io.impl;

import org.junit.Test;
import static org.junit.Assert.*;

import bfx.impl.QualRepr;

public class TestQualRepr {

	QualRepr repr = new QualRepr();
	
	private void test0(String in) {
		byte[] b = repr.stringToQual(in);
		for (byte i=0;i<b.length;i++) {
			assertEquals(i,b[i]);
		}
	}
	
	@Test
	public void test1() {
		test0("0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20");
		test0("   0 1 2 3 4 5 6 7\n 8 9 10 11 12 13 14 15 16 17 18 19 20");
		test0("0 1 2 3 4 5 6 7 8              9 10 11 12 13 14 15 16 17 18 19 20        ");
	}
	
}
