package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;


import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import utils.exception.TunneledException;

import bio.gff.GFF;
import bio.gff.GFFAlign;
import bio.gff.io.InvalidFormatException;

/*
 * Concrete implementation of GFFReader based on commons-io
 * 
 */
public class ComIOGFFReader extends AbstractGFFReader {
	
	private class GFFTransformer implements Transformer  {
		public Object transform(Object arg0) {
			String line = (String)arg0;
			return GFF.parseGFF3(line);
		}
	}
	
	private class LineFilter implements Predicate {
		public boolean evaluate(Object arg0) {
			String arg = (String)arg0;
			if (arg.startsWith("#") || arg.isEmpty())
				return false;
			else
				return true;
		}
		
	}
	
	private Iterator<GFFAlign> transform(Iterator<String> lit) {
		return (Iterator<GFFAlign>)
				new TransformIterator(
					new FilterIterator(lit,new LineFilter()),
					new GFFTransformer());
	}
	
	@Override
	public Iterator<GFFAlign> read(InputStream in) throws IOException {
		LineIterator lit = IOUtils.lineIterator(in, "ASCII");
		return transform(lit);
	}

	@Override
	public Iterator<GFFAlign> read(Reader in) throws IOException {
		LineIterator lit = new LineIterator(in);
		return transform(lit);
	}

}
