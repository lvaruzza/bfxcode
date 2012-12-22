package bfx.io.formats;

import bfx.io.SequenceFormat;
import bfx.io.SequenceReader;
import bfx.io.SequenceWriter;
import bfx.io.impl.SerializableSequenceReader;
import bfx.io.impl.SerializableSequenceWriter;

public class SerializableFormat extends SequenceFormat {

	@Override
	public SequenceReader getReader() {
		return new SerializableSequenceReader();
	}

	@Override
	public SequenceWriter getWriter() {
		return new SerializableSequenceWriter();
	}

	@Override
	public String getName() {
		return "serializable";
	}

	@Override
	public String[] getPreferredExtesionsList() {
		return new String[]{"javabin"};
	}

	@Override
	public String getPreferredExtesion() {
		return "javabin";
	}

}
