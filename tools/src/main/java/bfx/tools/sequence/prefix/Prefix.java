package bfx.tools.sequence.prefix;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.Sequence;
import bfx.io.SequenceSource;
import bfx.io.impl.FileSequenceSource;
import bfx.process.ProgressMeter;
import bfx.tools.Report;
import bfx.tools.Tool;
import bfx.utils.TextUtils;

import com.beust.jcommander.Parameter;

public class Prefix extends Tool {

	public static class PrefixReport extends Report {

		@Override
		public void writeHuman(PrintWriter out) {
			out.println(String.format("TODO"));
		}
	}

	private static Logger log = LoggerFactory.getLogger(Prefix.class);

	@Parameter(names = { "--input", "-i" }, description = "Input File", required = true)
	public String input;

	@Parameter(names = { "--inputFormat", "-if" }, description = "Input Format")
	public String inputFormat;

	@Parameter(names = { "--output", "-o" }, description = "Output File")
	public String output;


	@Override
	public void run() throws Exception {
		SequenceSource src = new FileSequenceSource(inputFormat, input);
		PrintStream out = output == null ? System.out : new PrintStream(new FileOutputStream(output));
		
		ProgressMeter pm = getProgressMeterFactory().get();

		src.setProgressMeter(pm);
		BytePrefixes prefix =  new BytePrefixes();
		
		PrefixReport report = new PrefixReport();

		// Create filterExpr
		pm.start("Calculating Prefix");
		for (Sequence seq : src) {
			prefix.add(seq.getSeq());
		}

		log.info(TextUtils.doubleLine());
		log.info("Finished.");
		log.info(TextUtils.doubleLine());

		for(Entry<byte[],Long> e:prefix) {
			out.print(new String(e.getKey()));
			out.print('\t');
			out.println(e.getValue());
		}
		out.close();
		report.write(System.out, Report.Format.HUMAN);
	}

	@Override
	public String getName() {
		return "prefix";
	}

}
