package bfx.tools.solid;

import com.beust.jcommander.Parameter;

import bfx.tools.Tool;
import bfx.tools.cli.Main;

public class CleanIndelGFF extends Tool {
	static {
		Main.addCommand("cleanIndelGFF", CleanIndelGFF.class);
	}

	public String getName() { return "cleanIndelGFF"; };
	
	@Parameter(names = "-input", description = "Input File",required=true)
	public String input;
	
	@Parameter(names = "-output", description = "Output File")
	public String output;
	
	
	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub
		
	}
}