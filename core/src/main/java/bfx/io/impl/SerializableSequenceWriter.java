package bfx.io.impl;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

import bfx.Sequence;
import bfx.io.SequenceWriter;

public class SerializableSequenceWriter extends SequenceWriter {

	@Override
	public void write(OutputStream out, Sequence seq) throws IOException {
		ObjectOutputStream objs = new ObjectOutputStream(out);
		objs.writeObject(seq);
	}

	@Override
	public void write(OutputStream out1, OutputStream out2, Sequence seq)
			throws IOException {
		write(out1,seq);
	}

	@Override
	public void write(Writer out1, Sequence seq) throws IOException {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void write(Writer out1, Writer out2, Sequence seq)
			throws IOException {
		write(out1,seq);

	}

	@Override
	public String getFormatName() {
		return "java.serializable";
	}

	@Override
	public void write(OutputStream out, Iterator<Sequence> data)
			throws IOException {
		ObjectOutputStream objs = new ObjectOutputStream(out);
		while(data.hasNext()) {
			objs.writeObject(data.next());
		}
	}

	@Override
	public void write(Writer out, Iterator<Sequence> data) throws IOException {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void write(OutputStream output, OutputStream ignored,
			Iterator<Sequence> data) throws IOException {
		write(output,data);
	}

	@Override
	public void write(Writer writer, Writer ignored, Iterator<Sequence> data)
			throws IOException {

		write(writer,data);
	}

}
