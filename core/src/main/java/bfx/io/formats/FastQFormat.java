package bfx.io.formats;

import bfx.io.SequenceFormat;
import bfx.io.SequenceReader;
import bfx.io.SequenceWriter;
import bfx.io.impl.FastQSequenceReader;
import bfx.io.impl.FastQSequenceWriter;
public class FastQFormat extends SequenceFormat {

	@Override
	public SequenceReader getReader() {
		return new FastQSequenceReader();
	}

	@Override
	public SequenceWriter getWriter() {
		return new FastQSequenceWriter();
	}

	@Override
	public String getName() {
		return "FastaQ";
	}

	@Override
	public String[] getPreferredExtesionsList() {
		return new String[]{"fastq","fastaq","fq","csfastq"};	
	}

	@Override
	public String getPreferredExtesion() {
		return "fastq";
	}

}
