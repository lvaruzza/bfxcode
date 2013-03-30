package bfx.tools.alignment;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import bfx.tools.Report;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class PrintInsertSize extends Tool {
	public static class InsertStatReport extends Report {
		double q1;
		double median;
		double[] decils;
		double q3;
		double iqd;
		long positive;
		long negative;
		long zero;
		
		public InsertStatReport(DescriptiveStatistics stats,long positive,long negative, long zero) {
			q1 = stats.getPercentile(25);
			median = stats.getPercentile(50);
			q3 = stats.getPercentile(75);
			iqd = q3-q1;
			decils = new double[10];
			for(int i=10;i<=100;i+=10) {
				decils[i/10-1]=stats.getPercentile(i); 
			}
			this.positive=positive;
			this.negative=negative;
			this.zero=zero;
		}


		@Override
		public void writeHuman(PrintWriter out) {
			out.println("Non zero insert statistics");
			out.println(String.format("Q1:         \t%.1f",q1));
			out.println(String.format("Median:     \t%.1f",median));
			out.println(String.format("Q3:         \t%.1f",q3));
			out.println(String.format("IQR:        \t%.1f",iqd));
			out.println(String.format("deciles:    \t%s",Arrays.toString(decils)));
			out.println("Insert orientation count");
			out.println(String.format("pos. count: \t%d",positive));
			out.println(String.format("neg. count: \t%d",negative));
			out.println(String.format("zero count: \t%d",zero));
		}
		
	}
	
	//private static Logger log = Logger.getLogger(Convert.class);
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;
	
	@Parameter(names = {"--output","-o"}, description = "Output File")
	public String output;

	@Override
	public void run() throws Exception {
		SAMFileReader reader = new SAMFileReader(new File(input));
		PrintStream out = output == null ? null : new PrintStream(output);
		DescriptiveStatistics stats = new DescriptiveStatistics();
		long positive=0;
		long negative=0;
		long zero=0;
		
		for (SAMRecord it:reader) {
			long x=it.getInferredInsertSize();
			if (out != null) out.println(x);
			if (x==0) {
				zero++;
			} else if (x>0) {
				positive++;
			} else {
				negative++;
			}
			if (x!=0) stats.addValue(Math.abs(x));
		}
		if (out!=null) out.close();
		reader.close();
		InsertStatReport report = new InsertStatReport(stats,positive,negative,zero);
		report.write(System.out, "human");
	}

	@Override
	public String getName() {
		return "printInsert";
	}
	
	@Override
	public String getGroup() {
		return "alignment";
	}

}
