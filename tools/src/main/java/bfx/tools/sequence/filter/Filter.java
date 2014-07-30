package bfx.tools.sequence.filter;

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
import bfx.io.impl.StreamSequenceSink;
import bfx.process.ProgressMeter;
import bfx.tools.Report;
import bfx.tools.Tool;
import bfx.utils.TextUtils;

import com.beust.jcommander.Parameter;

public class Filter extends Tool {

	public static class FilterReport extends Report {
		public long total;
		public long filtered;

		@Override
		public void writeHuman(PrintWriter out) {
			out.println(String.format("Total\t%d", total));
			out.println(String.format("Filtered\t%d (%.1f)", filtered, filtered
					* 100.0 / total));
		}
	}

	private static Logger log = LoggerFactory.getLogger(Filter.class);

	@Parameter(names = { "--input", "-i" }, description = "Input File", required = true)
	public String input;

	@Parameter(names = { "--qual", "-q" }, description = "Qual file (only applicable for fasta format)")
	public String qual;

	@Parameter(names = { "--inputFormat", "-if" }, description = "Input Format")
	public String inputFormat;

	@Parameter(names = { "--output", "-o" }, description = "Output File")
	public String output;

	@Parameter(names = { "--outputQual", "-oq" }, description = "Output Qual File (only appliable for fasta format)")
	public String outputQual;

	@Parameter(names = { "--outputFormat", "-of" }, description = "Output Format")
	public String outputFormat = "fasta";

	@Parameter(names = { "-e" }, description = "Filter by a generic Filter Expression (advanced)")
	public String filterExpr;

	@Parameter(names = { "-Q" }, description = "Filter by minimum mean quality")
	public Float minQuality = null;

	@Parameter(names = { "-L" }, description = "Filter by minimum read length")
	public Integer minLength = null;

	@Parameter(names = { "-N" }, description = "Filter sequence name (regex)")
	public String name = null;
	
	@Parameter(names = { "--logQual" }, description = "Log qual values to file")
	public String logQual;

	@Override
	public void run() throws Exception {
		SequenceSource src = new FileSequenceSource(inputFormat, input, qual);
		SequenceSink sink = getSequenceSink(outputFormat, output, outputQual);

		ProgressMeter pm = getProgressMeterFactory().get();

		src.setProgressMeter(pm);
		FilterReport report = new FilterReport();

		PrintStream logQualOut = null;

		FilterCompiler compiler = new FilterCompiler();

		// Create filterExpr

		FilterExpr filter;
		
		if (filterExpr != null) {
			filter = compiler.compile(filterExpr);
		} else {
			if (name != null) {
				log.info(String.format("Filter by name '%s'",name));
				filter = compiler.compile(String.format("name.matches(\"%s\")",name));
			} else if (minQuality != null && minLength != null) {
				filter = compiler.compile(String.format(
						"length >= %d && meanQuality >= %f", minLength,
						minQuality));
			} else if (minQuality != null) {
				filter = compiler.compile(String.format("meanQuality >= %f",
						minQuality));
			} else if (minLength != null) {
				filter = compiler.compile(String.format("length >= %d",
						minLength));
			} else
				throw new RuntimeException(
						"You need to specify -Q, -L or -e in command line.");
		}

		if (logQual != null)
			logQualOut = new PrintStream(new FileOutputStream(logQual));

		pm.start("Filtering");
		for (Sequence seq : src) {
			double m = seq.meanQuality();
			if (logQualOut != null)
				logQualOut.println(String.format("%s\t%.2f\t%d", seq.getId(),
						m, filter.filter(seq) ? 1 : 0));
			if (filter.filter(seq)) {
				sink.write(seq);
			} else {
				report.filtered++;
			}
			report.total++;
		}
		if (logQualOut != null)
			logQualOut.close();
		pm.finish();

		// in case of all filtered, zeroe the output file.
		if (report.filtered == report.total && output != null) {
			FileUtils.openOutputStream(new File(output)).write("".getBytes());
			if (outputQual != null) {
				FileUtils.openOutputStream(new File(outputQual)).write("".getBytes());
			}
		}

		log.info(TextUtils.doubleLine());
		log.info("Finished.");
		log.info(TextUtils.doubleLine());

		report.write(System.out, Report.Format.HUMAN);
	}

	@Override
	public String getName() {
		return "filter";
	}
	@Override
	public String getGroup() {
		return "sequence";
	}

}
