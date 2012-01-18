package bfx.tools.solid;

import bfx.Sequence;
import bfx.io.SequenceSink;
import bfx.io.SequenceSource;
import bfx.io.impl.FileSequenceSink;
import bfx.io.impl.FileSequenceSource;
import bfx.process.ProgressCounter;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class ColorDecode extends Tool {
	//private static Logger log = Logger.getLogger(ColorDecode.class);
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--qual","-q"}, description = "Qual File (only appliable for fasta format)")
	public String qual;
	
	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;
	
	@Parameter(names = {"--output","-o"}, description = "Output File")
	public String output;

	@Parameter(names = {"--outputQual","-oq"}, description = "Output Qual File (only appliable for fasta format)")
	public String outputQual;
	
	@Parameter(names = {"--outputFormat","-of"}, description = "Output Format")
	public String outputFormat;
	
	@Override
	public void run() throws Exception {
		SequenceSource src = new FileSequenceSource(inputFormat,input,qual);
		SequenceSink sink =  new FileSequenceSink(outputFormat,output,outputQual);

		ProgressCounter pc = getProgressCounter();
		pc.start(String.format("Conversion from Color Space to Base Space"));
		
		src.setProgressCounter(pc);
		for(Sequence seq: src) {
			Sequence baseSeq = bfx.seqenc.Color.colorDecode(seq);
			sink.write(baseSeq);
		}
		pc.finish();
	}

	@Override
	public String getName() {
		return "colorDecode";
	}

}
