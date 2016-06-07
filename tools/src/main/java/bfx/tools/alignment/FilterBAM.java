package bfx.tools.alignment;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamInputResource;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;

import java.io.File;

import bfx.process.ProgressMeter;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class FilterBAM extends Tool {
	public static interface BAMFilter {
		public boolean keep(SAMRecord align);
	}

	public static class RemoveMapped implements BAMFilter {

		@Override
		public boolean keep(SAMRecord align) {
			return !align.getReadUnmappedFlag();
		}

	}

	public static class RemoveUnmapped implements BAMFilter {

		@Override
		public boolean keep(SAMRecord align) {
			return align.getReadUnmappedFlag();
		}

	}

	// private static Logger log = Logger.getLogger(Convert.class);

	@Parameter(names = { "--remove", "-R" }, description = "Remove bam records acoording to: mapped or unmmaped.")
	public String filterName = "mapped";

	@Parameter(names = { "--input", "-i" }, description = "Input File", required = true)
	public String input;

	@Parameter(names = { "--output", "-o" }, description = "Output File", required = true)
	public String output;

	// @Parameter(names = {"--reportFormat", "-rf"}, description =
	// "Output Report Format")
	// public String reportFormat = "human";

	private static SAMFileWriterFactory factory = new SAMFileWriterFactory();

	@Override
	public void run() throws Exception {
		try (SamReader reader = SamReaderFactory.makeDefault().open(
				SamInputResource.of(new File(input)))) {

			File outputFile = new File(output);
			SAMFileHeader header = reader.getFileHeader();
			SAMFileWriter out = factory.makeBAMWriter(header, true, outputFile);
			BAMFilter filter = getFilter(filterName.toLowerCase());

			ProgressMeter pm = getProgressMeterFactory().get();
			pm.start(String.format("Filtering BAM file"));
			long count = 0;
			long unfiltered = 0;

			for (SAMRecord align : reader) {
				if (!filter.keep(align)) {
					out.addAlignment(align);
					unfiltered++;
				}
				count++;
				pm.incr(1);
			}
			out.close();

			pm.finish();

			System.out.println(String.format("Records %d", count));
			System.out.println(String.format("Filtered %d (%.2f%%)", count
					- unfiltered, (count - unfiltered) * 100.0 / count));
			System.out.println(String.format("Result %d (%.2f%%)", unfiltered,
					(unfiltered) * 100.0 / count));
		}

	}

	private BAMFilter getFilter(String filterName) {
		if (filterName.equals("mapped"))
			return new RemoveMapped();
		else if (filterName.equals("unmapped")) {
			return new RemoveUnmapped();
		} else {
			throw new RuntimeException(String.format("Filter '%s' not found.",
					filterName));
		}
	}

	@Override
	public String getName() {
		return "filterBAM";
	}

	@Override
	public String getGroup() {
		return "alignment";
	}

}
