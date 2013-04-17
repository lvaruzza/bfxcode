package bfx.run;


public class ExternalProgram {
	private Runner runner = new Runner();
	private String name;
	private String fullpath;
	
	private String getFullpath(String name) {
		return runner.slurp("/usr/bin/which",name);
	}
	public ExternalProgram(String name) {
		this.name = name;
		fullpath = getFullpath(name);
	}
	
	public void run(String... args) throws  RunnerException  {
		runner.run(fullpath,args);
	}

	public String getFullpath() {
		return fullpath;
	}
	public String getName() {
		return name;
	}

	
}
