package bfx.tool.assembly.scaffold;

import org.graphstream.graph.Graph;

import bfx.assembly.scaffold.BAMGraphBuilder;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class Scaffold extends Tool {

	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;
	
	@Parameter(names = {"--output","-o"}, description = "Output File")
	public String output;

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
		BAMGraphBuilder builder = new BAMGraphBuilder();
		Graph graph = builder.buildGraph(input, null);
		//BufferedWriter out = new BufferedWriter(new FileWriter(output));
		//GraphMLWriter<String,GraphEdge> writer= new GraphMLWriter<String,GraphEdge>();
		//writer.save(graph, out);
		
	}
	
}
