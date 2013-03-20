package bfx.tools.alignment;

import java.io.File;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class PrintInsertSize extends Tool {
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
		for (SAMRecord it:reader) {
			long x=it.getInferredInsertSize();
			if (out != null) out.println(x);
			stats.addValue(Math.abs(x));
		}
		if (out!=null) out.close();
		reader.close();
		
		double q1 = stats.getPercentile(25);
		double q2 = stats.getPercentile(50);
		double q3 = stats.getPercentile(75);
		
		System.out.println(q1);
		System.out.println(q2);
		System.out.println(q3);
		System.out.println(q3-q1);
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
