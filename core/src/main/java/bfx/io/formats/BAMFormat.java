package bfx.io.formats;

import bfx.io.SequenceFormat;
import bfx.io.SequenceReader;
import bfx.io.SequenceWriter;
import bfx.io.impl.BAMSequenceReader;
import bfx.io.impl.BAMSequenceWriter;
public class BAMFormat extends SequenceFormat {

	@Override
	public SequenceReader getReader() {
		return new BAMSequenceReader();
	}

	@Override
	public SequenceWriter getWriter() {
		return new BAMSequenceWriter();
	}

	@Override
	public String getName() {
		return "BAM";
	}

	@Override
	public String[] getPreferredExtesionsList() {
		return new String[]{"bam","sam"};	
	}

	@Override
	public String getPreferredExtesion() {
		return "bam";
	}

}
