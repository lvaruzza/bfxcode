package bfx.tools.sequence;

import bfx.Sequence;
import bfx.io.SequenceFormat;
import bfx.io.SequenceSink;
import bfx.io.SequenceSource;
import bfx.io.SequenceWriter;
import bfx.io.impl.FileSequenceSource;
import bfx.process.ProgressMeter;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class Split extends Tool {
	//private static Logger log = Logger.getLogger(Convert.class);
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--qual","-q"}, description = "Qual file (only applicable for fasta format)")
	public String qual;
	
	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;
	
	//@Parameter(names = {"--output","-o"}, description = "Output File")
	//public String output;

	
	//@Parameter(names = {"--outputFormat","-of"}, description = "Output Format")
	//public String outputFormat;
	
	@Override
	public void run() throws Exception {
		SequenceSource src = new FileSequenceSource(inputFormat, input, qual);
		
		/*if (outputFormat==null) {
			SequenceFormat outformat = SequenceFormat.getFormatForFile(input);
			outputFormat = outformat.getName();
		}*/
		
		ProgressMeter pm = this.getProgressMeterFactory().get();
		src.setProgressMeter(pm);

		pm.start("Reading input file");
		for(Sequence seq: src) {
			String name = seq.getId();
			SequenceSink out = SequenceSink.fromFile("fasta", name + ".fasta");
			out.write(seq);
			out.close();
		}
		pm.finish();
	}

	@Override
	public String getName() {
		return "split";
	}
	@Override
	public String getGroup() {
		return "sequence";
	}

}
