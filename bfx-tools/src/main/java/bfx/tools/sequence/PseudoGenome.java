package bfx.tools.sequence;

import bfx.Sequence;
import bfx.SequenceSet;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class PseudoGenome extends Tool {

	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;
	
	@Parameter(names = {"--outputGenome","-g"}, description = "Output Genome")
	public String output;

	@Parameter(names = {"--outputAnnotation","-a"}, description = "Output Annotation")
	public String annotation;
	
	@Override
	public void run() throws Exception {
		SequenceSet sequences = SequenceSet.fromFile(input,inputFormat);
		for(Sequence s: sequences) {
			
		}
	}

	@Override
	public String getName() {
		return "pseudoGenome";
	}

}
