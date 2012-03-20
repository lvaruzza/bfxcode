package bfx.io.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import bfx.GFF;
import bfx.io.GFFWriter;

/**
 * OutputStream GFFWriter
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class OSGFFWriter extends GFFWriter {

	@Override
	public void write(OutputStream output, Iterator<GFF> data)
			throws IOException {

		while(data.hasNext()) {
			write(output,data.next());
		}
	}
	
	@Override
	public void write(OutputStream out, GFF gff) throws IOException {		
		out.write(gff.getSeqid().getBytes());    				out.write('\t');
		out.write(gff.getSource().getBytes());   				out.write('\t');
		out.write(gff.getType().getBytes());     				out.write('\t');

		out.write(Long.toString(gff.getStart()).getBytes());	out.write('\t');
		out.write(Long.toString(gff.getEnd()).getBytes());		out.write('\t');
		out.write(Double.toString(gff.getScore()).getBytes());	out.write('\t');
		out.write(gff.getStrand());     						out.write('\t');
		if (gff.getPhase() == 0) {
			out.write('.');
		} else {
			out.write(Byte.toString(gff.getPhase()).getBytes());					
		}
		out.write('\t');
		writeAttributes(out,gff.getAttributes());
		out.write('\n');
	}

	private void writeAttributes(OutputStream out,
			Map<String, String> attributes) throws IOException {

		int size = attributes.size();
		int i = 0;
		
		for(Entry<String,String> attr:attributes.entrySet()) {
			out.write(attr.getKey().getBytes());
			out.write("=\"".getBytes());
			out.write(escapeString(attr.getValue()).getBytes());
			out.write('"');
			if (++i != size)
				out.write(';');
		}
	}

	public String escapeString(String in) {
		//TODO
		return in;
	}
	
	@Override
	public void write(Writer writer, Iterator<GFF> data) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
