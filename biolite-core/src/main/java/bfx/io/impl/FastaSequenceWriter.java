package bfx.io.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

import bfx.Sequence;
import bfx.io.SequenceWriter;
import bfx.utils.io.BaseSingleAndDualWriter;

public class FastaSequenceWriter extends BaseSingleAndDualWriter<Iterator<Sequence>> implements SequenceWriter {
	private int lineWidth;

	public FastaSequenceWriter(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public FastaSequenceWriter() {
		this(80);
	}
	
	private void writeHeader(OutputStream out,Sequence seq) throws IOException {
		out.write('>');
		out.write(seq.getId().getBytes());
		out.write(' ');
		out.write(seq.getId().getBytes());
		out.write('\n');		
	}
	
	public void writeSeq(OutputStream out,Sequence seq) throws IOException {
		writeHeader(out,seq);
		byte[] bs = seq.getSeq();
		
		for(int i=0;i<bs.length;i+=lineWidth) {
			out.write(bs, i, Math.min(lineWidth,bs.length-i));
			out.write('\n');
		}		
	}

	public void writeQual(OutputStream out,Sequence seq) throws IOException {
		writeHeader(out,seq);
		byte[] bs = seq.getQual();
		
		for(int i=0;i<bs.length;i+=lineWidth) {
			//TODO
			//out.write(bs, i, Math.min(lineWidth,bs.length-i));
			out.write('\n');
		}		
	}
	
	/* 
	 * Write only the sequence to a fasta file
	 */
	@Override
	public void write(OutputStream out, Iterator<Sequence> data)
			throws IOException {

		while(data.hasNext()) {
			writeSeq(out,data.next());
		}
	}

	/*
	 * Write only the sequence to a fasta file
	 */
	@Override
	public void write(Writer out, Iterator<Sequence> data) throws IOException {
	}

	/*
	 * Write the sequence and qual to a fasta/qual files
	 */
	@Override
	public void write(OutputStream outseq, OutputStream outqual,
			Iterator<Sequence> data) throws IOException {
		// TODO Auto-generated method stub
		
		while(data.hasNext()) {
			Sequence seq = data.next();
			writeSeq(outseq,seq);
			writeQual(outqual,seq);
		}
	}

	/*
	 * Write the sequence and qual to a fasta/qual files
	 */
	@Override
	public void write(Writer writer1, Writer writer2, Iterator<Sequence> data)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

}
