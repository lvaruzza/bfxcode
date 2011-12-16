package bfx.io.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

import bfx.ProgressCounter;
import bfx.Sequence;
import bfx.exceptions.FileProcessingIOException;
import bfx.exceptions.MultipleFilesProcessingIOException;
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
		
		for(int i=0;i<bs.length;i+=lineWidth) {
			//TODO
			//out.write(bs, i, Math.min(lineWidth,bs.length-i));
			out.write('\n');
		}		
	}

	public void writeQual(Writer out,Sequence seq) throws IOException {
		writeHeader(out,seq);
		byte[] bs = seq.getQual();
		
		for(int i=0;i<bs.length;i+=lineWidth) {
			//TODO!!!!!!!!!!!!!!
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
	public void write(File file1, Sequence seq) throws IOException{
		try {
			write(new FileOutputStream(file1),seq);
		} catch(IOException e) {
			throw new FileProcessingIOException(e,file1);
		}
	}

	@Override
	public void write(File file1, File file2, Sequence seq) throws IOException{
		try {
			write(new FileOutputStream(file1),new FileOutputStream(file2),seq);
		} catch(IOException e) {
			throw new MultipleFilesProcessingIOException(e,file1,file2);
		}
	}

	@Override
	public void write(OutputStream out1, OutputStream out2, Sequence seq) throws IOException {
		write(out1,seq);		
		writeQual(out2,seq);	
	}

	@Override
	public String[] getPreferedExtensions() {
		return FastaSequenceReader.fastaExtensions;
	}


	@Override
	public void write(Writer out1, Writer out2, Sequence seq)
			throws IOException {
		write(out1,seq);
		writeQual(out2,seq);
	}
	
	private ProgressCounter pc;	
	@Override
	public void setProgressCounter(ProgressCounter pc) {
		this.pc = pc;
	}

	@Override
	public String getFormatName() {
		return "fasta";
	}
	
}
