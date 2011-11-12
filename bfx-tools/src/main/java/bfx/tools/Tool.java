package bfx.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;



public abstract class Tool {
	public abstract void run() throws Exception;

	public abstract String getName();

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
}
