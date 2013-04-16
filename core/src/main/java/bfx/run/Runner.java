package bfx.run;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.ArrayUtils;

public class Runner {
	
	private OutputStream output = System.out;
	
	public void setOutput(OutputStream out) {
		this.output = out;
	}
	public void run(ProcessBuilder builder) throws InterruptedException, IOException {
		builder.redirectErrorStream(true);
		Process proc=builder.start();
		InputStream procIn = proc.getInputStream();
		StreamGobbler globber = new StreamGobbler(procIn,output);
		proc.waitFor();
		globber.start();
		if  (proc.exitValue()!=0) {
			throw new RuntimeException(String.format("Program '%s' failed, exit value: %d",
					builder.command(),proc.exitValue()));
		}
	}
	
	public void run(String command) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder(command);
		run(builder);	
	}
	
	public void run(String program,String... args) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder((String[])ArrayUtils.add(args, 0,program));
		run(builder);	
	}

	public void run(String[] cmdv) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder(cmdv);
		run(builder);
	}
	
}
