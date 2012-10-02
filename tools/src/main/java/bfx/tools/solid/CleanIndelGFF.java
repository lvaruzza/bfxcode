package bfx.tools.solid;

import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class CleanIndelGFF extends Tool {
	public String getName() { return "cleanIndelGFF"; };
	
	@Parameter(names = "-input", description = "Input File",required=true)
	public String input;
	
	@Parameter(names = "-output", description = "Output File")
	public String output;
	
	
	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getGroup() {
		return "solid";
	}
	
}