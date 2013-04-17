package bfx.run;

public class RunnerException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String command;
	private String stderr;
	private int exitValue=0;
	
	public RunnerException(Exception e, String command, String error) {
		super(e);
		this.command = command;
		this.stderr = error;
	}
	
	public RunnerException(int exitValue, String command, String error) {
		this.command = command;
		this.stderr = error;
		this.exitValue = exitValue;
	}

	@Override
	public String getMessage() {
		if (exitValue !=0 ) {
			return String.format("Failed to run '%s' command, exit code = %d.\nProgram stderr:\n%s", 
					exitValue,command,stderr);
			
		} else {
			return String.format("Failed to run '%s' command. Error: %s\nProgram stderr:\n%s", 
					super.getMessage(),command,stderr);
		}
	}
	
}
