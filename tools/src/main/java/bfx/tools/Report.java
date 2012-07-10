package bfx.tools;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * General Report supporting several output formats.
 * 
 * Should be inherit by the concrete Reports
 * @author varuzza
 *
 */
public abstract class Report {
	/**
	 * Write the report in Human Readable Format
	 * 
	 * @param out - Output Writer
	 * 
	 */
	public abstract void writeHuman(PrintWriter out);

	/**
	 * List of supported formats
	 * @author varuzza
	 *
	 */
	public static enum Format {
		JSON, HUMAN
	};

	protected ObjectMapper getMapper() {
		return new ObjectMapper();
	}
	
	public void writeJSON(final OutputStream out) throws IOException {
		out.write(getMapper().writeValueAsBytes(this));
		out.write('\n');
		out.flush();
	}

	public void write(OutputStream out, String format) throws IOException {
		write(out,getFormat(format));
	}
	
	public void write(OutputStream out, Format format) throws IOException {
		switch (format) {
		case HUMAN:
			PrintWriter pr = new PrintWriter(out); 
			pr.println("=====================================================================");
			pr.println("# Result                                                            #");
			pr.println("=====================================================================");
			pr.println();
			writeHuman(pr);
			pr.println();
			pr.println("=====================================================================");
			pr.println("# End                                                               #");
			pr.println("=====================================================================");
			pr.flush();
			break;
		case JSON:
			writeJSON(out);
			break;
		}
	}

	public static Format getFormat(String outputFormat) {
		if (outputFormat == null)
			return Format.HUMAN;

		if (outputFormat.equals("json"))
			return Format.JSON;
		if (outputFormat.equals("human"))
			return Format.HUMAN;
		throw new RuntimeException(String.format("Invalid output format: '%s'",outputFormat));
	}

}
