package bfx.tools;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.codehaus.jackson.map.ObjectMapper;

public abstract class Report {
	/*
	 * Write the report in Human Readable Format
	 */
	public abstract void writeHuman(PrintWriter out);

	public static enum Format {
		JSON, HUMAN
	};

	private static ObjectMapper mapper = new ObjectMapper();

	public void writeJSON(OutputStream out) throws IOException {
		mapper.writeValue(out, this);
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
			pr.println("# Finished                                                          #");
			pr.println("=====================================================================");
			pr.flush();
			break;
		case JSON:
			writeJSON(out);
			break;
		}
	}

}
