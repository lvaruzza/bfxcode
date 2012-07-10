package bfx.tools.sequence;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import bfx.Sequence;
import bfx.io.SequenceSource;
import bfx.process.ProgressMeter;
import bfx.tools.Report;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class SequenceStat extends Tool {
	public String getName() {return "seqstat"; };
	
	/**
	 * Perposition statistics
	 * 
	 * @author varuzza
	 *
	 */
	public static class PositionStat {
		public int count = 0;
		public double avgQual = 0;
		public int minQual = Integer.MAX_VALUE;
		public int maxQual = 0;
	}
	
	public static class StatReport extends Report {

		public long totalLen;
		public int seqCount;
		public double averageLength;
		public Map<Character,Long> symbols;
		public int minSequenceLength;
		public int maxSequenceLength;
		public double averageQual;
		public int minQual;
		public int maxQual;
		public PositionStat[] positionStat;
		
		private static DecimalFormat df = new DecimalFormat();
		
		/**
		 * Resulting Report
		 */
		public StatReport() {
			symbols = new TreeMap<Character,Long>();
			minSequenceLength = Integer.MAX_VALUE;
			maxSequenceLength = 0;			
		}
		
		@Override
		public void writeHuman(PrintWriter pr) {
			pr.println(              "Sequences Length Information:");
			pr.println(              "\tTotal Length       \t" + df.format(totalLen));
			pr.println(              "\tNumber of sequences\t" + df.format(seqCount));
			pr.println(String.format("\tAverage Length     \t%.1f",averageLength));
			pr.println(             ("\tLarger sequence    \t" + df.format(maxSequenceLength)));
			pr.println(             ("\tSmaller sequence   \t"+ df.format(minSequenceLength)));
			pr.println();
			
			pr.println("Symbol frequency:");
			for(Entry<Character,Long> e: symbols.entrySet()) {
				pr.println(String.format("\t%c\t%d (%.2f%%)",e.getKey(),e.getValue(),e.getValue()*100.0/totalLen));
			}
			
			pr.println();
			pr.println(              "Sequences Quality Information:");
			pr.println(String.format("\tAverage Quality    \t%.1f",averageQual));
			pr.println(String.format("\t    Max Quality    \t%d",maxQual));
			pr.println(String.format("\t    Min Quality    \t%d",minQual));
			pr.println();

			pr.println();
			pr.println(              "Quality by Position:");
			int i = 0;
			pr.println("Pos\tReads\tAvg. Q\tMin Q\tMax Q");
			for(PositionStat ps: positionStat) {
				pr.println(String.format("%d\t%d\t%.2f\t%d\t%d",
						i++,ps.count,
						ps.avgQual,ps.minQual,ps.maxQual));
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
		SequenceSource sequences = SequenceSource.fromFile(inputFormat,input,qual);

		StatReport result = new StatReport();
		ProgressMeter pm = getProgressMeterFactory().get();
		sequences.setProgressMeter(pm);
		pm.start("Reading Sequences");
		double qualSum = 0;
		result.minQual =  Integer.MAX_VALUE;
		result.maxQual = 0;
		Vector<PositionStat> posStat = new Vector<PositionStat>();
		
		for (Sequence s: sequences) {
			result.seqCount++;
			result.totalLen += s.length();
			if (s.length() > result.maxSequenceLength)
				result.maxSequenceLength = s.length();
			if (s.length() < result.minSequenceLength)
				result.minSequenceLength = s.length();
			
			if (posStat.size() < s.length()) posStat.setSize(s.length());
			
			// Update symbol frequency table
			byte[] seq = s.getSeq();
			for(byte b: seq) {
				if (result.symbols.containsKey((char)b)) {
					result.symbols.put((char)b, result.symbols.get((char)b) + 1);
				} else {
					result.symbols.put((char)b, 1l);					
				}
			}
			
			
			byte[] qual = s.getQual();
			int i = 0;
			for(byte q: qual) {
				PositionStat ps = posStat.get(i);
				if (ps == null) ps = new PositionStat();
				
				// Geral Max and Min qual
				if (q > result.maxQual) result.maxQual = q;
				if (q < result.minQual) result.minQual = q;
				
				// Position Max and Min qual
				if (q > ps.maxQual) ps.maxQual = q;
				if (q < ps.minQual) ps.minQual = q;
				
				qualSum += q;
				ps.count += 1;
				
				// Use avgQual as a accumulator
				ps.avgQual += q;
				posStat.set(i, ps);
				i++;
			}			
		}
		pm.finish();
		
		// Calculate the average quality
		for(int i=0;i<posStat.size();i++) {
			PositionStat ps = posStat.get(i);
			ps.avgQual = ps.avgQual / ps.count;
		}
		
		result.averageQual = qualSum * 1.0 / result.totalLen;
		result.averageLength = ((double)result.totalLen) /  result.seqCount;
		result.positionStat = posStat.toArray(new PositionStat[posStat.size()]);
		result.write(getStdOut(output), outputFormat);
	}

}
