package bfx.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.beust.jcommander.Parameter;

import bfx.process.ProgressCounter;



public abstract class Tool {
	protected ProgressCounter pc;
	
	public abstract void run() throws Exception;
	
	public abstract String getName();

	
	@Parameter(names = {"--verbose","-v"}, description = "Verbose output")
	public boolean verbose = false;
	
	public OutputStream getStdOut(String filename) throws FileNotFoundException {
		if (filename == null) {
			return System.out;
		} else {
			return new FileOutputStream(filename);
		}
	}
	
	protected void execute() {
		try {
			run();
		} catch(Exception e) {
			System.err.println(String.format("Error execution tool '%': %s",
						getName(),e.getMessage()));
			
			e.printStackTrace(System.err);
		}
	}
	
	public void setProgressCounter(ProgressCounter pc) {
		this.pc = pc;
	}
	
	public ProgressCounter getProgressCounter() {
		return pc;
	}
}
