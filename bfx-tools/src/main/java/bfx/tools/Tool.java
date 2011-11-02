package bfx.tools;

import java.io.IOException;
import java.io.OutputStream;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


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
	
}
