package bfx.run;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.ArrayUtils;

public class Runner {
	
	private OutputStream output = System.out;
	private ByteArrayOutputStream err;
	
	public void setOutput(OutputStream out) {
		this.output = out;
	}

	public String slurp(ProcessBuilder builder)  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			run(builder,baos);
			return baos.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void run(ProcessBuilder builder,OutputStream out) throws RunnerException  {
		builder.redirectErrorStream(true);
		Process proc;
		StreamGobbler globber;
		StreamGobbler errGlober;
		try {
			proc = builder.start();
			InputStream procIn = proc.getInputStream();
			globber = new StreamGobbler(procIn,out);
			err = new ByteArrayOutputStream();
			errGlober = new StreamGobbler(proc.getErrorStream(),err);
			globber.start();
			errGlober.start();
			proc.waitFor();
		} catch (Exception e) {
			throw new RunnerException(e,builder.
						command().toString(),
						err.toString());
		}
		if  (proc.exitValue()!=0) {
			throw new RunnerException(proc.exitValue(),builder.
					command().toString(),
					err.toString());
		}
	}
	
	public void run(String command) throws RunnerException {
		ProcessBuilder builder = new ProcessBuilder(command);
		run(builder,output);	
	}
	
	public void run(String program,String... args) throws RunnerException {
		ProcessBuilder builder = new ProcessBuilder((String[])ArrayUtils.add(args, 0,program));
		run(builder,output);	
	}

	public void run(String[] cmdv) throws IOException, InterruptedException, RunnerException {
		ProcessBuilder builder = new ProcessBuilder(cmdv);
		run(builder,output);
	}
	
	public String slurp(String command)  {
		ProcessBuilder builder = new ProcessBuilder(command);
		return slurp(builder);
	}

	public String slurp(String program,String... args)  {
		ProcessBuilder builder = new ProcessBuilder((String[])ArrayUtils.add(args, 0,program));
		return slurp(builder);	
	}
	
}
