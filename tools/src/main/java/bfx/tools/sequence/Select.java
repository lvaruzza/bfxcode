package bfx.tools.sequence;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import bfx.Sequence;
import bfx.io.SequenceFormat;
import bfx.io.SequenceSink;
import bfx.io.SequenceSource;
import bfx.io.impl.FileSequenceSource;
import bfx.process.ProgressMeter;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class Select extends Tool {
	//private static Logger log = Logger.getLogger(Convert.class);

	@Parameter(names = { "--ids", "-I" }, description = "List of sequence Ids", 
			   variableArity = true)
	public List<String> ids = new ArrayList<String>();

	@Parameter(names = "--not", description = "Get unmatched id's")
	boolean invert = false;
	
	@Parameter(names = { "--idList", "-L" }, description = "File with a list of ids")
	public String idListFile;
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--qual","-q"}, description = "Qual file (only applicable for fasta format)")
	public String qual;
	
	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;
	
	@Parameter(names = {"--output","-o"}, description = "Output File")
	public String output;

	@Parameter(names = {"--outputQual","-oq"}, description = "Output Qual File")
	public String outputQual;
	
	@Parameter(names = {"--outputFormat","-of"}, description = "Output Format")
	public String outputFormat;
	
	@Override
	public void run() throws Exception {
		SequenceSource src = new FileSequenceSource(inputFormat, input, qual);
		
		if (outputFormat==null) {
			SequenceFormat outformat = SequenceFormat.getFormatForFile(input);
			outputFormat = outformat.getName();
		}
		Map<String,Boolean> idMap = new HashMap<String,Boolean>();

		for (String id:ids) {
			idMap.put(id, true);
		}
		
		if (idListFile != null ) {
			List<String> fid = FileUtils.readLines(new File(idListFile));
			for (String id:fid) {
				idMap.put(id, true);
			}
		}
		
		ProgressMeter pm = this.getProgressMeterFactory().get();
		src.setProgressMeter(pm);
		
		SequenceSink out = getSequenceSink(outputFormat,output,outputQual);
		
		pm.start("Reading input file");
		for(Sequence seq: src) {
			if (invert ^ idMap.containsKey(seq.getId()))
				out.write(seq);
		}
		pm.finish();
	}

	@Override
	public String getName() {
		return "select";
	}
	@Override
	public String getGroup() {
		return "sequence";
	}

}
