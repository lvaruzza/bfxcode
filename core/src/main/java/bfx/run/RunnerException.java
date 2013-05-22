package bfx.run;

public class RunnerException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String command;
	private int exitValue=0;
	
	public RunnerException(Exception e, String command) {
		super(e);
		this.command = command;
	}
	
	public RunnerException(int exitValue, String command) {
		this.command = command;
		this.exitValue = exitValue;
	}

	@Override
	public String getMessage() {
		if (exitValue !=0 ) {
			return String.format("Failed to run '%s' command, exit code = %d.", 
					exitValue,command);
			
		} else {
			return String.format("Failed to run '%s' command. Error: %s\n.", 
					command,super.getMessage());
		}
	}
	
}
