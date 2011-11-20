package bfx.tools.sequence;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import bfx.ProgressCounter;
import bfx.Sequence;
import bfx.io.SequenceFormats;
import bfx.io.SequenceReader;
import bfx.tools.Report;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class SequenceStat extends Tool {
	public String getName() {return "sequenceStat"; };
	
	public static class StatReport extends Report {

		public long totalLen;
		public int seqCount;
		public double averageLength;
		public Map<Character,Long> symbols;
		public int minSequenceLength;
		public int maxSequenceLength;
		public double averageQual;
		
		private static DecimalFormat df = new DecimalFormat();
		
		@Override
		public void writeHuman(PrintWriter pr) {
			pr.println(              "Sequences Length Information:");
			pr.println(              "\tTotal Length       \t" + df.format(totalLen));
			pr.println(              "\tNumber of sequences\t" + df.format(seqCount));
			pr.println(String.format("\tAverage Length     \t%.1f",averageLength));
			pr.println(             ("\tLarger sequence    \t" + df.format(maxSequenceLength)));
			pr.println(             ("\tSmaller sequence   \t"+ df.format(minSequenceLength)));
			pr.println();
			
			pr.println(              "Sequences Quality Information:");
			pr.println(String.format("\tAverage Quality    \t%.1f",averageQual));
			pr.println();
			
			pr.println("Symbol frequency:");
			for(Entry<Character,Long> e: symbols.entrySet()) {
				pr.println(String.format("\t%c\t%d (%.2f%%)",e.getKey(),e.getValue(),e.getValue()*100.0/totalLen));
			}
			pr.println();
			pr.flush();
		}
	}

	
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--qual","-q"}, description = "Qual file (only appliable for fasta foramt)")
	public String qual;
	
	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;
	
	@Parameter(names = {"--output","-o"}, description = "Output Report File")
	public String output;

	@Parameter(names = {"--outputFormat","-of"}, description = "Output Report Format")
	public String outputFormat;
	
	
	@Override
	public void run() throws Exception {
		SequenceReader reader = SequenceFormats.getReader(input,inputFormat);
		Iterator<Sequence> it;
		
		if (qual != null && inputFormat != null && !inputFormat.equals("fasta")) {
			throw new RuntimeException("You can only use specify a qual file for fasta format");
		}
		
		if (qual != null) {
			it = reader.read(input,qual);
		} else {
			it = reader.read(input);
		}
		
		StatReport result = new StatReport();
		result.symbols = new TreeMap<Character,Long>();
		result.minSequenceLength = Integer.MAX_VALUE;
		result.maxSequenceLength = 0;
		ProgressCounter pc = getProgressCounter();
		pc.setTick(1000*1000);
		
		while (it.hasNext()) {
			Sequence s = it.next();
			pc.incr(1);
			
			result.seqCount++;
			result.totalLen += s.length();
			if (s.length() > result.maxSequenceLength)
				result.maxSequenceLength = s.length();
			if (s.length() < result.minSequenceLength)
				result.minSequenceLength = s.length();
			
			// Update symbol frequency table
			byte[] seq = s.getSeq();
			for(byte b: seq) {
				if (result.symbols.containsKey((char)b)) {
					result.symbols.put((char)b, result.symbols.get((char)b) + 1);
				} else {
					result.symbols.put((char)b, 1l);					
				}
			}
			long qualSum = 0;
			
			byte[] qual = s.getQual();
			for(byte q: qual) {
				qualSum += q;
			}			
			result.averageQual = qualSum * 1.0 / result.seqCount;
		}
		
		result.averageLength = ((double)result.totalLen) /  result.seqCount;
		result.write(getStdOut(output), outputFormat);
	}

}
