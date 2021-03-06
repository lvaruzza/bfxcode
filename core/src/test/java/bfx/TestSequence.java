package bfx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import bfx.Sequence;
import static org.junit.Assert.*;

public class TestSequence {
	private static Logger log = LoggerFactory.getLogger(TestSequence.class);
	
	private void testHeader(String header,String id,String comment) {
		log.debug(String.format("Testing '%s'",header));
		String[] p1 = Sequence.parseHeader(header);
		assertEquals(id,p1[0]);
		assertEquals(comment,p1[1]);		
	}
	
	@Test
	public void parseHeader() {
		testHeader("1","1","");
		testHeader("1 ","1","");
		testHeader("1 fifofu","1","fifofu");
		testHeader("marafo fifofu  xxx 1 )**(*","marafo","fifofu  xxx 1 )**(*");
	}
}
