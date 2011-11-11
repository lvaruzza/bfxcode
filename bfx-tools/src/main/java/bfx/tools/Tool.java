package bfx.tools;



public abstract class Tool {
	public abstract void run() throws Exception;

	public abstract String getName();

	
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
