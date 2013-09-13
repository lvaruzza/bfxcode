package bfx.tool.assembly.scaffold;

import bfx.assembly.scaffold.bam.BAMReader;
import bfx.assembly.scaffold.edges.PairsToEdges;
import bfx.assembly.scaffold.edges.SumEdges;
import bfx.assembly.scaffold.edges.SuperEdge;
import bfx.assembly.scaffold.technology.IonTorrentTechnology;
import bfx.assembly.util.Table;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;


public class Scaffold extends Tool {

	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;
	
	@Parameter(names = {"--output","-o"}, description = "Output File")
	public String output;

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
		SumEdges sumedges = new SumEdges();
		BAMReader reader = new BAMReader(input);
		PairsToEdges merger = new PairsToEdges(new IonTorrentTechnology(),mqFilter);		
		merger.setConsumer(sumedges);
		reader.read(merger);
		Table table=new Table(output);

		for(SuperEdge se:sumedges) {
			table.printRow(se.getLeft(),
					se.getRight(),
					se.getCount(),
					se.getSumMQ(),
					se.getDistanceMedian(),
					se.getDistanceIQD(),
					se.getDistanceIQD()/se.getDistanceMedian(),
					se.isReverse() ? "R" : "F");
		}
		
		
	}
	
}
