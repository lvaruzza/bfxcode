package bfx.tools.circos;

import java.io.PrintStream;

import bfx.Sequence;
import bfx.io.SequenceSource;
import bfx.io.impl.FileSequenceSource;
import bfx.process.ProgressMeter;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class Karyotype extends Tool {
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
			out.println(String.format("chr - %s %s 0 %d blue",seq.getId(),seq.getId(),seq.length()));
		}
		out.close();
		pm.finish();
	}

	@Override
	public String getName() {
		return "karyotype";
	}
	
	@Override
	public String getGroup() {
		return "circos";
	}

}
