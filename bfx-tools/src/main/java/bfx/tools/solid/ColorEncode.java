package bfx.tools.solid;

import org.apache.log4j.Logger;

import bfx.ProgressCounter;
import bfx.Sequence;
import bfx.SequenceSink;
import bfx.SequenceSource;
import bfx.io.impl.FileSequenceSink;
import bfx.io.impl.FileSequenceSource;
import bfx.tools.Tool;
import bfx.utils.TextUtils;

import com.beust.jcommander.Parameter;

public class ColorEncode extends Tool {
	private static Logger log = Logger.getLogger(ColorEncode.class);
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;
	
	@Parameter(names = {"--output","-o"}, description = "Output File")
	public String output;

	@Parameter(names = {"--outputQual","-oq"}, description = "Output Qaul File (only appliable for fasta format)")
	public String outputQual;
	
	@Parameter(names = {"--outputFormat","-of"}, description = "Output Format")
	public String outputFormat;
	
	@Override
	public void run() throws Exception {
		SequenceSource src = new FileSequenceSource(input,inputFormat);
		SequenceSink sink =  new FileSequenceSink(output,outputFormat);

		log.info(TextUtils.doubleLine());
		log.info(String.format("Started sequences conversion from base space to color space"));
		log.info(TextUtils.doubleLine());
		
		ProgressCounter pc = getProgressCounter();
		src.setProgressCounter(pc);
		for(Sequence seq: src) {
			sink.write(seq);
		}
		pc.finish();
		
		log.info(TextUtils.doubleLine());
		log.info("Finished.");
		log.info(TextUtils.doubleLine());
	}

	@Override
	public String getName() {
		return "convert";
	}

}
