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

public class SubSet extends Tool {
	private static Logger log = LoggerFactory.getLogger(SubSet.class);
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--percent","-p"}, description = "Percent of sequencies to be selected",required=true)
	public float percent;

	@Parameter(names = "--seed", description = "Seed of random number generator (advanced)")
	public long seed;

	@Parameter(names = "--same", description = "Sucessive runs of this program will select the same random select sequences from the file (this will the the seed to a default value)")
	public boolean useDefaultSeed;

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
		percent = (float) (percent / 100.0);
		
		log.info(TextUtils.doubleLine());
		log.info(String.format("Started subsampling %.1f%% of sequences",percent*100.0));
		log.info(TextUtils.doubleLine());
		
		ProgressMeter pm = getProgressMeterFactory().get();
		
		src.setProgressMeter(pm);
		pm.start("Reading Sequences");
		Random rnd = new Random();
		for(Sequence seq: src) {
			if (rnd.nextFloat() < percent)
			sink.write(seq);
		}
		pm.finish();
		
		log.info(TextUtils.doubleLine());
		log.info("Finished.");
		log.info(TextUtils.doubleLine());
	}

	@Override
	public String getName() {
		return "subset";
	}
	@Override
	public String getGroup() {
		return "sequence";
	}

}
