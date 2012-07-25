package bfx.tools.sequence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.Sequence;
import bfx.io.SequenceSink;
import bfx.io.SequenceSource;
import bfx.io.impl.FileSequenceSink;
import bfx.io.impl.FileSequenceSource;
import bfx.process.ProgressMeter;
import bfx.tools.Report;
import bfx.tools.Tool;
import bfx.utils.TextUtils;

import com.beust.jcommander.Parameter;

public class Filter extends Tool {
	
	public static class FilterReport extends Report{
		public long total;
		public long filtered;
		
		@Override
		public void writeHuman(PrintWriter out) {
			out.println(String.format("Total\t%d",total));
			out.println(String.format("Filtered\t%d (%.1f)",filtered,filtered*100.0/total));
		}
	}
	
	private static Logger log = LoggerFactory.getLogger(Filter.class);
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;


	@Parameter(names = {"--qual","-q"}, description = "Qual file (only applicable for fasta format)")
	public String qual;
	
	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;
	
	@Parameter(names = {"--output","-o"}, description = "Output File")
	public String output;

	@Parameter(names = {"--outputQual","-oq"}, description = "Output Qaul File (only appliable for fasta format)")
	public String outputQual;
	
	@Parameter(names = {"--outputFormat","-of"}, description = "Output Format")
	public String outputFormat;

	@Parameter(names = {"--minMeanQaul","--mean"}, description = "Minimal Mean Quality")
	public float mmq;

	@Parameter(names = {"--logQual"}, description = "Log qual values to file")
	public String logQual;
	
	@Override
	public void run() throws Exception {
		SequenceSource src = new FileSequenceSource(inputFormat,input,qual);
		SequenceSink sink =  new FileSequenceSink(outputFormat,output,outputQual);
				
		ProgressMeter pm = getProgressMeterFactory().get();
		
		src.setProgressMeter(pm);
		FilterReport report = new FilterReport();
		
		PrintStream logQualOut = null;
		
		if(logQual != null)
			logQualOut = new PrintStream(new FileOutputStream(logQual));
		
		for(Sequence seq: src) {
			double m = seq.meanQuality(); 
			if (logQualOut != null)
				logQualOut.println(String.format("%s\t%.2f\t%d",seq.getId(),m,m>=mmq));
			if ( m >= mmq) {
				sink.write(seq);
			} else {
				report.filtered++;
			}
			report.total++;
		}
		if (logQualOut != null) logQualOut.close();
		
		// in case of all filtered, zeroe the output file.
		if (report.filtered == report.total) {
			FileUtils.openOutputStream(new File(output)).write("".getBytes());
			if (outputQual!=null) {
				FileUtils.openOutputStream(new File(outputQual)).write("".getBytes());
			}
		}
		pm.finish();
		
		log.info(TextUtils.doubleLine());
		log.info("Finished.");
		log.info(TextUtils.doubleLine());
		
		report.write(System.out, Report.Format.HUMAN);
	}

	@Override
	public String getName() {
		return "filter";
	}

}
