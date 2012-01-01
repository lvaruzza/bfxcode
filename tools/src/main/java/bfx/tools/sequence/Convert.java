package bfx.tools.sequence;

import org.apache.log4j.Logger;

import bfx.ProgressCounter;
import bfx.io.SequenceFormats;
import bfx.io.SequenceReader;
import bfx.io.SequenceWriter;
import bfx.tools.Tool;
import bfx.utils.TextUtils;

import com.beust.jcommander.Parameter;

public class Convert extends Tool {
	private static Logger log = Logger.getLogger(Convert.class);
	
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
		SequenceReader sr = SequenceFormats.getReader(input,inputFormat);
		SequenceWriter sw = SequenceFormats.getWriter(output,outputFormat);

		inputFormat = sr.getFormatName();
		outputFormat = sw.getFormatName();
		
		
		log.info(TextUtils.doubleLine());
		log.info(String.format("Started sequences conversion from %s format to %s format",inputFormat,outputFormat));
		log.info(TextUtils.doubleLine());
		
		ProgressCounter pc = getProgressCounter();
		sw.setProgressCounter(pc);
		sw.write(output,outputQual,sr.read(input,qual));
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
