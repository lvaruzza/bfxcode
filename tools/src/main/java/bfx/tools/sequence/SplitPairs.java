package bfx.tools.sequence;

import java.io.File;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bfx.Sequence;
import bfx.io.SequenceFormat;
import bfx.io.SequenceSink;
import bfx.io.SequenceSource;
import bfx.io.impl.FileSequenceSource;
import bfx.process.ProgressMeter;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class SplitPairs extends Tool {
	//private static Logger log = Logger.getLogger(Convert.class);
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--qual","-q"}, description = "Qual file (only applicable for fasta format)")
	public String qual;
	
	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;

	@Parameter(names = {"--left","--output1","-o1","-l"}, description = "Output File left",required=true)
	public String left;

	@Parameter(names = {"--right","--output2","-o2","-r"}, description = "Output File right",required=true)
	public String right;
	
	
	//@Parameter(names = {"--output","-o"}, description = "Output File")
	//public String output;

	
	@Parameter(names = {"--outputFormat","-of"}, description = "Output Format")
	public String outputFormat;
	
	@Override
	public void run() throws Exception {
		SequenceSource src = new FileSequenceSource(inputFormat, input, qual);
		//Pattern leftpat = Pattern.compile("(.*?)//1");
		//Pattern rightpat = Pattern.compile("(.*?)//2");
		Pattern pairseq = Pattern.compile("(.*?)(/[12]|\\.[rf])");
		
		if (outputFormat==null) {
			SequenceFormat outformat = SequenceFormat.getFormatForFile(input);
			outputFormat = outformat.getName();
		}
		
		ProgressMeter pm = this.getProgressMeterFactory().get();
		src.setProgressMeter(pm);
		SequenceSink lw = SequenceSink.fromFile(outputFormat, new File(left));
		SequenceSink rw = SequenceSink.fromFile(outputFormat, new File(right));
		
		pm.start("Reading input file");
		int count=0;
		
		for(Sequence seq: src) {
			Matcher matcher = pairseq.matcher(seq.getId());
			/*System.out.println(seq.getId());
			System.out.println(matcher.matches());
			System.out.println(matcher);*/
			if (matcher.matches()) { 
				if(count%2==0) 
					lw.write(seq);
				else
					rw.write(seq);
				count++;
			}
		}
		
		pm.finish();
		lw.close();
		rw.close();
		
	}

	@Override
	public String getName() {
		return "splitPairs";
	}
	@Override
	public String getGroup() {
		return "sequence";
	}

}
