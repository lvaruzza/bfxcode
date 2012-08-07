package bfx.io.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Iterator;

import bfx.Sequence;
import bfx.io.SequenceWriter;
import bfx.utils.ByteUtils;

/**
 * Fasta format sequence writer.
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class FastaSequenceWriter extends SequenceWriter {
	private int lineWidth;

	/**
	 * Construct a new SequenceWriter
	 * 
	 * This writer will break the sequence in lineWidth characters per line.
	 * 
	 * @param lineWidth
	 */
	public FastaSequenceWriter(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	/**
	 * Construct a new SequenceWriter
	 * 
	 * This writer will break the sequence in 80 characters per line.
	 * 
	 */
	public FastaSequenceWriter() {
		this(80);
	}
	
	private void writeHeader(OutputStream out,Sequence seq) throws IOException {
		out.write('>');
		out.write(seq.getId().getBytes());
		if(seq.getComments().length() != 0) {
			out.write(' ');
			out.write(seq.getComments().getBytes());
		}
		out.write('\n');	
	}

	private void writeHeader(Writer out,Sequence seq) throws IOException {
		out.write('>');
		out.write(seq.getId());
		if(seq.getComments().length() != 0) {
			out.write(' ');
			out.write(seq.getComments());
		}
		out.write('\n');	
	}
	
	@Override
	public void write(OutputStream out,Sequence seq) throws IOException {
		writeHeader(out,seq);
		byte[] bs = seq.getSeq();
		
		for(int i=0;i<bs.length;i+=lineWidth) {
			out.write(bs, i, Math.min(lineWidth,bs.length-i));
			out.write('\n');
		}		
	}

	@Override
	public void write(Writer out, Sequence seq) throws IOException {
		writeHeader(out,seq);
		String bs = new String(seq.getSeq());
		
		for(int i=0;i<bs.length();i+=lineWidth) {
			out.write(bs, i, Math.min(lineWidth,bs.length()-i));
			out.write('\n');
		}				
	}
	
	
	public void writeQual(OutputStream out,Sequence seq) throws IOException {
		writeHeader(out,seq);
		byte[] bs = seq.getQual();
		PrintStream pr = new PrintStream(out);
		for(int i=0;i<bs.length;i+=lineWidth) {
			ByteUtils.printBytes(pr,bs, i, Math.min(lineWidth,bs.length-i));
			out.write('\n');
		}		
	}

	public void writeQual(Writer out,Sequence seq) throws IOException {
		writeHeader(out,seq);
		byte[] bs = seq.getQual();
		
		for(int i=0;i<bs.length;i+=lineWidth) {
			ByteUtils.printBytes(out,bs, i, Math.min(lineWidth,bs.length-i));
			out.write('\n');
		}		
	}
	
	/**
	 *  
	 * Write only the sequence to a fasta file
	 */
	@Override
	public void write(OutputStream out, Iterator<Sequence> data)
			throws IOException {

		while(data.hasNext()) {
			pc.incr(1);
			write(out,data.next());
		}
	}

	/*
	 * Write only the sequence to a fasta file
	 */
	@Override
	public void write(Writer out, Iterator<Sequence> data) throws IOException {
		while(data.hasNext()) {
			pc.incr(1);
			write(out,data.next());
		}
	}

	/*
	 * Write the sequence and qual to a fasta/qual files
	 */
	@Override
	public void write(OutputStream outseq, OutputStream outqual,
			Iterator<Sequence> data) throws IOException {

		while(data.hasNext()) {
			pc.incr(1);
			write(outseq,outqual,data.next());
		}
	}

	/*
	 * Write the sequence and qual to a fasta/qual files
	 */
	@Override
	public void write(Writer outseq, Writer outqual, Iterator<Sequence> data)
			throws IOException {
		while(data.hasNext()) {
			pc.incr(1);
			write(outseq,outqual,data.next());
		}
	}

	@Override
	public void write(OutputStream out1, OutputStream out2, Sequence seq) throws IOException {
		write(out1,seq);		
		writeQual(out2,seq);	
	}

	@Override
	public String[] getPreferedExtensionsList() {
		return FastaSequenceReader.fastaExtensions;
	}


	@Override
	public void write(Writer out1, Writer out2, Sequence seq)
			throws IOException {
		write(out1,seq);
		writeQual(out2,seq);
	}

	@Override
	public String getFormatName() {
		return "fasta";
	}
	
}
