package bfx.io.formats;

import bfx.io.SequenceFormat;
import bfx.io.SequenceReader;
import bfx.io.SequenceWriter;
import bfx.io.impl.FastaSequenceReader;
import bfx.io.impl.FastaSequenceWriter;

public class FastaFormat extends SequenceFormat {

	@Override
	public SequenceReader getReader() {
		return new FastaSequenceReader();
	}

	@Override
	public SequenceWriter getWriter() {
		return new FastaSequenceWriter();
	}

	@Override
	public String getName() {
		return "fasta";
	}

	@Override
	public String[] getPreferredExtesionsList() {
		return new String[]{"fasta","fa","csfasta"};
	}

	@Override
	public String getPreferredExtesion() {
		return "fasta";
	}

}
