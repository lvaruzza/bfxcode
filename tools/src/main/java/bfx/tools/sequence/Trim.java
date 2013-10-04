package bfx.tools.sequence;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.Sequence;
import bfx.io.SequenceSink;
import bfx.io.SequenceSource;
import bfx.io.impl.FileSequenceSink;
import bfx.io.impl.FileSequenceSource;
import bfx.process.ProgressMeter;
import bfx.tools.Tool;
import bfx.utils.TextUtils;

import com.beust.jcommander.Parameter;

public class Trim extends Tool {
	private static Logger log = LoggerFactory.getLogger(Trim.class);
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--length","-l"}, description = "Output Length",required=true)
	public int newLength;


	@Parameter(names = {"--color","-C"}, description = "Trim color-space sequence")
	public boolean color;

	@Parameter(names = {"--qual","-q"}, description = "Qual file (only applicable for fasta format)")
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
		
		log.info(TextUtils.doubleLine());
		log.info(String.format("Started trimming to %d",newLength));
		log.info(TextUtils.doubleLine());
		
		ProgressMeter pm = getProgressMeterFactory().get();
		
		src.setProgressMeter(pm);
		pm.start("Reading Sequences");

		for(Sequence seq: src) {
			sink.write(seq.trimRight(newLength,color));
		}
		pm.finish();
		
		log.info(TextUtils.doubleLine());
		log.info("Finished.");
		log.info(TextUtils.doubleLine());
	}

	@Override
	public String getName() {
		return "trim";
	}
	@Override
	public String getGroup() {
		return "sequence";
	}

}
