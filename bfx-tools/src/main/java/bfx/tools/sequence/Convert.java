package bfx.tools.sequence;

import bfx.io.SequenceFormats;
import bfx.io.SequenceReader;
import bfx.io.SequenceWriter;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class Convert extends Tool {

	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;
	
	@Parameter(names = {"--output","-o"}, description = "Output File")
	public String output;

	@Parameter(names = {"--outputFormat","-of"}, description = "Output Format")
	public String outputFormat;
	
	@Override
	public void run() throws Exception {
		SequenceReader sr = SequenceFormats.getReader(input,inputFormat);
		SequenceWriter sw = SequenceFormats.getWriter(output,outputFormat);

		sw.write(output, sr.read(input));
	}

	@Override
	public String getName() {
		return "pseudoGenome";
	}

}
