package bfx.tool.assembly.scaffold;

import bfx.assembly.scaffold.bam.BAMReader;
import bfx.assembly.scaffold.edges.InsertStats;
import bfx.technology.Technology;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;


public class Scaffold extends Tool {

	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;
	
	@Parameter(names = {"--output","-o"}, description = "Output File")
	public String output;

	@Parameter(names = {"--technology","-T"}, description = "Sequencing Technology",required=true)
	public String techName;
	
	@Parameter(names = {"--min-mq","-q"}, description = "Minimum Map Quality")
	public int mqFilter = 10;
	
	//@Parameter(names = {"--reportFormat", "-rf"}, description = "Output Report Format")
	//public String reportFormat = "human";
	

	@Override
	public String getName() {
		return "scaffold";
	}

	@Override
	public String getGroup() {
		return "assembly";
	}

	@Override
	public void run() throws Exception {
		Technology tech = Technology.get(techName);
		
		if (tech == null) {
			throw new RuntimeException(String.format("Invalid technology name '%s",techName));
		}
		
		BAMReader reader = new BAMReader(input);
		InsertStats is = new InsertStats();		
		reader.read(is);
	}
	
}
