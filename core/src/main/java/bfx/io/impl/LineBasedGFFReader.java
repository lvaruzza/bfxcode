package bfx.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import bfx.GFF;
import bfx.impl.GFFParser;
import bfx.io.GFFReader;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;


/*
 * Concrete implementation of GFFReader based on commons-io
 * 
 */
public class LineBasedGFFReader extends GFFReader {
	
	private class GFFTransformer implements Function<String,GFF>  {
		public GFF apply(String line) {
			return GFFParser.parseGFF3(line);
		}
	}
	
	private class LineFilter implements Predicate<String> {
		public boolean apply(String arg) {
			if (arg.startsWith("#") || arg.isEmpty())
				return false;
			else
				return true;
		}
		
	}
	
	private Iterator<GFF> transform(Iterator<String> lit) {
		return Iterators.transform(Iterators.filter(lit, new LineFilter()),new GFFTransformer());

		/*		return (Iterator<GFFImpl>)
				new TransformIterator(
					new FilterIterator(lit,new LineFilter()),
					new GFFTransformer());*/
	}
	
	@Override
	public Iterator<bfx.GFF> read(InputStream in) throws IOException {
		LineIterator lit = IOUtils.lineIterator(in, "ASCII");
		return transform(lit);
	}

	@Override
	public Iterator<bfx.GFF> read(Reader in) throws IOException {
		LineIterator lit = new LineIterator(in);
		return transform(lit);
	}
}
