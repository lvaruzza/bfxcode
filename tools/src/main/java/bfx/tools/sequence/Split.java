package bfx.tools.sequence;

import java.io.File;

import bfx.Sequence;
import bfx.io.SequenceFormats;
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

	
	@Parameter(names = {"--outputFormat","-of"}, description = "Output Format")
	public String outputFormat;
	
	@Override
	public void run() throws Exception {
		SequenceSource src = new FileSequenceSource(inputFormat, input, qual);
	
		if (outputFormat==null) {
			// TODO: create this method
			//outputFormat = SequenceFormats.getFormatForFile(input);
			outputFormat = "fasta";
		}
		
		ProgressMeter pm = this.getProgressMeterFactory().get();
		src.setProgressMeter(pm);

		SequenceWriter sw = SequenceFormats.getWriter(outputFormat);
		
		pm.start("Reading input file");
		for(Sequence seq: src) {
			sw.write(new File(seq.getId() + "." + sw.getPreferedExtension()),seq);
		}
		pm.finish();
	}

	@Override
	public String getName() {
		return "split";
	}

}
