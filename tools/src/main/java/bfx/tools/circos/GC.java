package bfx.tools.circos;

import java.io.PrintStream;

import bfx.Sequence;
import bfx.io.SequenceSource;
import bfx.io.impl.FileSequenceSource;
import bfx.process.ProgressMeter;
import bfx.seqenc.DNA;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class GC extends Tool {
	//private static Logger log = Logger.getLogger(Convert.class);
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

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
	
	@Parameter(names = {"--window","-w"}, description = "Window Size")
	public int wsize = 200;
	
	@Override
	public void run() throws Exception {
		SequenceSource src = new FileSequenceSource(inputFormat, input, qual);
		PrintStream out = new PrintStream(this.getStdOut(output));
		if (output == null) {
			this.getProgressMeterFactory().disable();
		}

		ProgressMeter pm = this.getProgressMeterFactory().get();
		src.setProgressMeter(pm);
		pm.start("Reading input file");
		for(Sequence seq: src) {
			byte[] s = seq.getSeq();
			for(int i=0;i<s.length;i+=wsize) {
				int end = Math.min(s.length, i+wsize);
				double gc=DNA.GC(s,i,end);
				out.println(String.format("%s %d %d %.2f",seq.getId(),i,end,100.0*gc));
			}
		}
		out.close();
		pm.finish();
	}

	@Override
	public String getName() {
		return "GC";
	}
	
	@Override
	public String getGroup() {
		return "circos";
	}

}
