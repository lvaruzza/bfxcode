package bfx.tools.sequence.filter;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestExprCompiler {
	private FilterCompiler compiler = new FilterCompiler();
	
	@Test
	public void testLength() {
		FilterExpr filter1 = compiler.compile("length > 10");
		assertTrue(filter1.filter(11, 0));
		assertFalse(filter1.filter(9, 0));
		assertTrue(filter1.filter(11, 20));
		assertFalse(filter1.filter(9, 7.8));
	}
	
	public void testLengthAndQual() {
		FilterExpr filter1 = compiler.compile("length > 10 && meanQuality>20");
		assertTrue(filter1.filter(11, 21));
		assertFalse(filter1.filter(9, 21));
		assertFalse(filter1.filter(12, 11));

		FilterExpr filter2 = compiler.compile("length > 10 || meanQuality>20");
		assertTrue(filter2.filter(11, 21));
		assertTrue(filter2.filter(9, 21));
		assertTrue(filter2.filter(12, 11));
		
		// does not make much sense but is valid
		FilterExpr filter3 = compiler.compile("length > meanQuality");
		assertTrue(filter3.filter(11, 10));
		
	}
}
