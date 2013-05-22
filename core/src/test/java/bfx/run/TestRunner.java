package bfx.run;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestRunner {
	
	@Test
	public void testLs() throws RunnerException {
		System.out.println("Test ls");
		Runner.run("ls");
	}
	
	//@Test
	public void testSlurp() throws RunnerException {
		String out = Runner.slurp("echo echo");
		assertEquals("echo",out);
	}

}
