package bfx.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import bfx.process.ProgressMeter;
import bfx.process.ProgressMeterFactory;

import com.beust.jcommander.Parameter;



public abstract class Tool {
	protected ProgressMeterFactory pcf;
	
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
	
	public void setProgressMeterFactory(ProgressMeterFactory pcf) {
		this.pcf = pcf;
	}
	
	public ProgressMeterFactory getProgressMeterFactory() {
		return pcf;
	}
}
