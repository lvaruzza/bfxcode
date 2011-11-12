package bfx.tools.sequence;

import java.io.PrintWriter;
import java.util.Iterator;

import bfx.Sequence;
import bfx.io.SequenceFormats;
import bfx.io.SequenceReader;
import bfx.tools.Report;
import bfx.tools.Tool;
import bfx.tools.cli.Main;

import com.beust.jcommander.Parameter;

public class SequenceStat extends Tool {
	
	static {
		Main.addCommand("sequenceStat", SequenceStat.class);
	}

	public String getName() {return "sequenceStat"; };
	
	public static class StatReport extends Report {

		public long totalLen;
		public int seqCount;

		@Override
		public void writeHuman(PrintWriter pr) {
			pr.println("Total Length = " + totalLen);
			pr.println("Number of sequences = " + seqCount);
		}
	}

	
	
	@Parameter(names = "-input", description = "Input File",required=true)
	public String input;
	
	@Parameter(names = "-report", description = "Output Report File")
	public String output;
	
	
	@Override
	public void run() throws Exception {
		// String outputFormat = config.get("outputFormat","human");

		SequenceReader reader = SequenceFormats.getReader(input);
		Iterator<Sequence> it = reader.read(input);

		StatReport result = new StatReport();
		while (it.hasNext()) {
			Sequence s = it.next();
			result.seqCount++;
			result.totalLen += s.length();
		}
		result.write(getStdOut(output), Report.Format.HUMAN);
	}

}
