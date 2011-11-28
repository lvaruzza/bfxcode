package bfx.tools.sequence;

import java.io.File;

import bfx.Sequence;
import bfx.SequenceBuilder;
import bfx.SequenceSet;
import bfx.impl.SequenceBuilderListImpl;
import bfx.io.SequenceWriter;
import bfx.io.impl.FastaSequenceWriter;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class PseudoGenome extends Tool {

	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;
	
	@Parameter(names = {"--outputGenome","-g"}, description = "Output Genome")
	public String outputGenome;

	@Parameter(names = {"--genomeName","-n"}, description = "Genome Name")
	public String name;
	
	@Parameter(names = {"--outputAnnotation","-a"}, description = "Output Annotation")
	public String annotation;
	
	@Override
	public void run() throws Exception {
		SequenceSet sequences = SequenceSet.fromFile(input,inputFormat);
		SequenceBuilder sb = new SequenceBuilderListImpl();
		
		for(Sequence s: sequences) {
			sb.append(s);
		}
		Sequence r = sb.getConstQual(name, (byte)0);
		SequenceWriter sw = new FastaSequenceWriter();
		sw.write(new File(outputGenome), r);
	}

	@Override
	public String getName() {
		return "pseudoGenome";
	}

}
