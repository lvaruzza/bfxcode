package bfx.run;

import java.io.IOException;

import org.junit.Test;

public class TestRunner {
	public static Runner runner = new Runner();
	
	@Test
	public void testLs() throws IOException, InterruptedException {
		System.out.println("Test ls");
		runner.run("ls");
	}
}
