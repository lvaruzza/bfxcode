package bfx.run;


public class ExternalProgram {
	private String name;
	private String fullpath;
	
	private String getFullpath(String name) {
		return Runner.slurp("/usr/bin/which",name);
	}
	public ExternalProgram(String name) {
		this.name = name;
		fullpath = getFullpath(name);
	}
	
	public void run(String... args) throws  RunnerException  {
		Runner runner=new Runner(fullpath,args);
		runner.run();
	}
	

	public String getFullpath() {
		return fullpath;
	}
	public String getName() {
		return name;
	}

	
}
