package bfx.tools.solid;

import org.apache.commons.lang.ArrayUtils;

import bfx.Sequence;
import bfx.io.SequenceSink;
import bfx.io.SequenceSource;
import bfx.io.impl.FileSequenceSink;
import bfx.io.impl.FileSequenceSource;
import bfx.process.ProgressMeter;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class ColorEncode extends Tool {
	//private static Logger log = Logger.getLogger(ColorEncode.class);
	
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
	public String outputFormat = "fasta";

	@Parameter(names = {"--prefix","-P"}, description = "Add a prefix character before every color-space sequence")
	public String prefix = null;
	
	@Override
	public void run() throws Exception {
		SequenceSource src = new FileSequenceSource(inputFormat,input,qual);
		SequenceSink sink = getSequenceSink(outputFormat, output, outputQual);

		ProgressMeter pm = getProgressMeterFactory().get();
		pm.start(String.format("Conversion from Base Space to Color Space"));
		src.setProgressMeter(pm);
		for(Sequence seq: src) {
			Sequence colorSeq = bfx.seqenc.Color.colorEncode(seq);
			if (prefix != null) {
				if (prefix.length() != 1)
					throw new RuntimeException("You can only add a single letter prefix (to be consistent with SOLiD data)");
				byte prfx = (byte)prefix.charAt(0);
				Sequence prefixed = colorSeq.changeSeq(ArrayUtils.add(colorSeq.getSeq(),0,prfx));
				sink.write(prefixed);
			} else {
				sink.write(colorSeq);
			}
			
		}
		pm.finish();
	}

	@Override
	public String getName() {
		return "colorEncode";
	}
	@Override
	public String getGroup() {
		return "solid";
	}

}
