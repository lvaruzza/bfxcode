package bfx.run;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestRunner {
	public static Runner runner = new Runner();
	
	@Test
	public void testLs() throws RunnerException {
		System.out.println("Test ls");
		runner.run("ls");
	}
	
	@Test
	public void testSlurp() throws RunnerException {
		String out = runner.slurp("echo echo");
		assertEquals("echo",out);
	}

}
