package bfx.tools.sequence;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import bfx.Sequence;
import bfx.io.SequenceFormat;
import bfx.io.SequenceReader;
import bfx.tools.Report;
import bfx.tools.Tool;
import bfx.tools.ToolConfiguration;

public class SequenceStat extends Tool {
	
	public static class StatReport implements Report {

		public long totalLen;
		public int seqCount;
		@Override
		public void writeReport(Writer out) {
			PrintWriter pr = new PrintWriter(out);
			pr.println("Total Length = " + totalLen);
			pr.println("Number of sequences = " + seqCount);
		}
		
		
	}
	
	@Override
	public void run() throws Exception {
		ToolConfiguration config = this.getConfig();
		String input = config.get("input");
		String output = config.get("output");
		String outputFormat = config.get("outputFormat","human");
		
		SequenceReader reader = SequenceFormat.getReader(input);
		Iterator<Sequence> it = reader.read(input);
	
		StatReport result = new StatReport();
		while(it.hasNext()) {
			Sequence s = it.next();
			result.seqCount++;
			result.totalLen += s.length();
		}
		writeResult(output,outputFormat,result);
	}
}
