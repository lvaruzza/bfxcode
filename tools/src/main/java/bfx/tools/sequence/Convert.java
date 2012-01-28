package bfx.tools.sequence;

import bfx.io.SequenceFormats;
import bfx.io.SequenceReader;
import bfx.io.SequenceWriter;
import bfx.process.ProgressMeter;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class Convert extends Tool {
	//private static Logger log = Logger.getLogger(Convert.class);
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--qual","-q"}, description = "Qual file (only applicable for fasta format)")
	public String qual;
	
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
		SequenceReader sr = SequenceFormats.getReaderForFile(input,inputFormat);
		SequenceWriter sw = SequenceFormats.getWriterForFile(output,outputFormat);

		inputFormat = sr.getFormatName();
		outputFormat = sw.getFormatName();
				
		ProgressMeter pm = getProgressMeterFactory().get();
		sw.setProgressMeter(pm);
		pm.start(String.format("Sequences conversion from %s format to %s format",inputFormat,outputFormat));
		sw.write(output,outputQual,sr.read(input,qual));
		pm.finish();
	}

	@Override
	public String getName() {
		return "convert";
	}

}
