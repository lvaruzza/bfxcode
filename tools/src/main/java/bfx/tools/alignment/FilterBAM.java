package bfx.tools.alignment;

import java.io.File;

import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMFileWriterFactory;
import net.sf.samtools.SAMRecord;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class FilterBAM extends Tool {
	public static interface BAMFilter {
		public boolean keep(SAMRecord align);
	}
	
	public static class RemoveMapped implements BAMFilter {

		@Override
		public boolean keep(SAMRecord align) {
			return align.getMateUnmappedFlag();
		}
		
	}

	public static class RemoveUnammaped implements BAMFilter {

		@Override
		public boolean keep(SAMRecord align) {
			return !align.getMateUnmappedFlag();
		}
		
	}
	
	//private static Logger log = Logger.getLogger(Convert.class);

	@Parameter(names = {"--filter","-F"}, description = "Filter type: mapped or unmmaped. It will remove mapped or unmapped reads, respectively.")
	public String filterName="mapped";

	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;
	
	@Parameter(names = {"--output","-o"}, description = "Output File",required=true)
	public String output;

	//@Parameter(names = {"--reportFormat", "-rf"}, description = "Output Report Format")
	//public String reportFormat = "human";
	
	private static SAMFileWriterFactory factory = new SAMFileWriterFactory();
	
	@Override
	public void run() throws Exception {
		SAMFileReader reader = new SAMFileReader(new File(input));
		File outputFile = new File(output);
		SAMFileHeader header = reader.getFileHeader();
		SAMFileWriter out = factory.makeBAMWriter(header, true, outputFile);
		BAMFilter filter = getFilter(filterName);
		
		for (SAMRecord align:reader) {
			if(!filter.keep(align)) {
				out.addAlignment(align);
			}
		}
		
		out.close();
		reader.close();
	}

	private BAMFilter getFilter(String filterName2) {
		return null;
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
