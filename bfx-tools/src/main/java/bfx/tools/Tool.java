package bfx.tools;

import java.io.Serializable;

public abstract class Tool {
	private ToolConfiguration config;
	
	public void setConfig(ToolConfiguration config) {
		this.config = config;
	}
	
	public ToolConfiguration getConfig() { return config; };
	
	public abstract void run() throws Exception;

	public String getName() {
		return this.getClass().getSimpleName();
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
	
	public void writeResult(String filename,String format,Report report) {
		// TODO
	}
}
