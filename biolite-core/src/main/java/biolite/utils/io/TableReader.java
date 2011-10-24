package biolite.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Iterators;

public class TableReader extends BaseReader<Iterator<String[]>> {
	private String sep;
	
	public TableReader() {
		this("\t");
	}
	
	public TableReader(String sep) {
		this.sep = sep;
	}
	
	@Override
	public Iterator<String[]> read(InputStream in) throws IOException {
		return Iterators.transform(IOUtils.lineIterator(in, "ASCII"), new LineSplitter(sep));
	}

	@Override
	public Iterator<String[]> read(Reader reader) throws IOException {
		return Iterators.transform(IOUtils.lineIterator(reader), new LineSplitter(sep));
	}

}
