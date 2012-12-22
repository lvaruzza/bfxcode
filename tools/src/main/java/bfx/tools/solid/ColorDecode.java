package bfx.tools.solid;

import java.util.Arrays;

import bfx.Sequence;
import bfx.io.SequenceSink;
import bfx.io.SequenceSource;
import bfx.io.impl.FileSequenceSink;
import bfx.io.impl.FileSequenceSource;
import bfx.process.ProgressMeter;
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
	public String outputFormat = "fastq";
	
	@Parameter(names={"--trim","-T"},description="")
	public boolean trimFirst = false;
	
	@Override
	public void run() throws Exception {
		SequenceSource src = new FileSequenceSource(inputFormat,input,qual);
		SequenceSink sink = getSequenceSink(outputFormat, output, outputQual);

		ProgressMeter pm = getProgressMeterFactory().get();
		pm.start(String.format("Conversion from Color Space to Base Space"));
		
		src.setProgressMeter(pm);
		for(Sequence seq: src) {
			Sequence baseSeq = bfx.seqenc.Color.colorDecode(seq);
			if (trimFirst) {
				byte[] s = baseSeq.getSeq();
				Sequence trimmed = baseSeq.changeSeq(Arrays.copyOfRange(s, 1, s.length));
				sink.write(trimmed);
			} else {
				sink.write(baseSeq);
			}
		}
		pm.finish();
	}

	@Override
	public String getName() {
		return "colorDecode";
	}
	@Override
	public String getGroup() {
		return "solid";
	}

}
