package bfx.tools.sequence;

import java.io.File;

import bfx.Sequence;
import bfx.SequenceBuilder;
import bfx.SequenceSet;
import bfx.impl.SequenceBuilderListImpl;
import bfx.impl.SequenceConstQualImpl;
import bfx.io.SequenceWriter;
import bfx.io.impl.FastaSequenceWriter;
import bfx.tools.Tool;
import bfx.utils.TextUtils;

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

	@Parameter(names = {"--spacerSize","-ss"}, description = "Number of N's to be put between each sequence")
	public int spacerSize = 100;
	
	@Parameter(names = {"--outputAnnotation","-a"}, description = "Output Annotation")
	public String annotation;
	
	@Override
	public void run() throws Exception {
		SequenceSet sequences = SequenceSet.fromFile(input,inputFormat);
		SequenceBuilder sb = new SequenceBuilderListImpl();
		Sequence spacer = new SequenceConstQualImpl("spacer",TextUtils.times('N',spacerSize),(byte)0);
		
		for(Sequence s: sequences) {
			sb.append(s);
			sb.append(spacer);
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
