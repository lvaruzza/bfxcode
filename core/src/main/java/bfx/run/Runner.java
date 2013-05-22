package bfx.run;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runner {
	private static Logger log = LoggerFactory.getLogger(Runner.class);
	private ProcessBuilder builder;
	
	public Runner(String command) {
		builder = new ProcessBuilder(command);
	}
	
	public Runner(String program,String... args) {
		builder = new ProcessBuilder((String[])ArrayUtils.add(args, 0,program));
	}

	public Runner(String[] cmdv)  {
		builder = new ProcessBuilder(cmdv);
	}
	
	public String slurp()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			run(baos);
		} catch (RunnerException e) {
			throw new RuntimeException(e);
		}
		return baos.toString();
	}

	public void run() throws RunnerException  {
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		try {
			run(System.out,err);
		} catch(RunnerException e) {
			log.debug(String.format("Error running command %s.\nProgram stderr:\n%s",
					builder.command().toString(),err.toString()));
			throw new RunnerException(e,builder.
					command().toString());			
		}		
	}

	public void run(OutputStream out) throws RunnerException  {
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		try {
			run(out,err);
		} catch(RunnerException e) {
			log.debug(String.format("Error running command %s.\nProgram stderr:\n%s",
					builder.command().toString(),err.toString()));
			throw new RunnerException(e,builder.
					command().toString());			
		}
	}
	
	public void run(OutputStream out,OutputStream err) throws RunnerException  {
		Process proc;
		StreamGobbler globber;
		StreamGobbler errGlober;
		try {
			proc = builder.start();
			InputStream procIn = proc.getInputStream();

			globber = new StreamGobbler(procIn,out);
			globber.start();
			
			errGlober = new StreamGobbler(proc.getErrorStream(),err);
			errGlober.start();
			
			proc.waitFor();
		} catch (Exception e) {
			throw new RunnerException(e,builder.command().toString());
		}
		if  (proc.exitValue()!=0) {
			throw new RunnerException(proc.exitValue(),builder.
					command().toString());
		}
	}

	
	public static String slurp(String command)  {
		return new Runner(command).slurp();
	}

	public static void run(String command) throws RunnerException  {
		new Runner(command).run();
	}
	

	public static String slurp(String program,String... args)   {
		return new Runner(program,args).slurp();
	}
	
}
