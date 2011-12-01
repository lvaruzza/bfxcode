package bfx.io.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

import bfx.QualRepr;
import bfx.Sequence;
import bfx.exceptions.FileProcessingIOException;
import bfx.impl.FastQRepr;
import bfx.io.SequenceWriter;
import bfx.utils.io.BaseSingleAndDualWriter;

public class FastQSequenceWriter extends BaseSingleAndDualWriter<Iterator<Sequence>> implements SequenceWriter {
	private QualRepr qualrepr;
	
	public FastQSequenceWriter(QualRepr qualrepr) {
		this.qualrepr = qualrepr;
	}

	public FastQSequenceWriter() {
		this(new FastQRepr(FastQRepr.FastqEncoding.SANGER));
	}
	
	private void writeHeader(OutputStream out,Sequence seq) throws IOException {
		out.write('@');
		out.write(seq.getId().getBytes());
		if(seq.getComments().length() != 0) {
			out.write(' ');
			out.write(seq.getComments().getBytes());
		}
		out.write('\n');	
	}

	private void writeHeader(Writer out,Sequence seq) throws IOException {
		out.write('@');
		out.write(seq.getId());
		if(seq.getComments().length() != 0) {
			out.write(' ');
			out.write(seq.getComments());
		}
		out.write('\n');	
	}
	
	public void write(OutputStream out,Sequence seq) throws IOException {
		writeHeader(out,seq);
		out.write(seq.getSeq());
		out.write('\n');
		out.write("+\n".getBytes());
		out.write(qualrepr.qualToTextBytes(seq.getQual()));
		out.write('\n');		
	}

	@Override
	public void write(Writer out, Sequence seq) throws IOException {
		writeHeader(out,seq);
		out.write(new String(seq.getSeq()));
		out.write('\n');
		out.write("+\n");
		out.write(qualrepr.qualToTextString(seq.getQual()));
		out.write('\n');				
	}
	
	/* 
	 * Write only the sequence to a fastQ file
	 */
	@Override
	public void write(OutputStream out, Iterator<Sequence> data)
			throws IOException {

		while(data.hasNext()) {
			write(out,data.next());
		}
	}

	/*
	 * Write only the sequence to a fastQ file
	 */
	@Override
	public void write(Writer out, Iterator<Sequence> data) throws IOException {
		while(data.hasNext())
			write(out,data.next());
	}

	/*
	 * Invalid Method
	 */
	@Override
	public void write(OutputStream outseq, OutputStream outqual,
			Iterator<Sequence> data) throws IOException {

		throw new RuntimeException("This method does not work with fastQ format.");
	}

	/*
	 * Invalid Method
	 */
	@Override
	public void write(Writer writer1, Writer writer2, Iterator<Sequence> data)
			throws IOException {
		throw new RuntimeException("This method does not work with fastQ format.");
	}

	@Override
	public void write(File file1, Sequence seq) throws IOException{
		try {
			write(new FileOutputStream(file1),seq);
		} catch(IOException e) {
			throw new FileProcessingIOException(e,file1);
		}
	}

	@Override
	public void write(File file1, File file2, Sequence seq) throws IOException{
		throw new RuntimeException("This method does not work with fastQ format.");
	}

	@Override
	public void write(OutputStream out1, OutputStream out2, Sequence seq) throws IOException {
		throw new RuntimeException("This method does not work with fastQ format.");
	}

	@Override
	public String[] getPreferedExtensions() {
		return FastQSequenceReader.fastQExtensions;
	}


	@Override
	public void write(Writer out1, Writer out2, Sequence seq)
			throws IOException {
		throw new RuntimeException("This method does not work with fastQ format.");
	}

}
