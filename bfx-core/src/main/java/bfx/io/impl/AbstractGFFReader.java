package bfx.io.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;

import bfx.exceptions.FileProcessingException;
import bfx.exceptions.URLProcessingException;
import bfx.impl.GFFAlign;
import bfx.io.GFFReader;
import bfx.utils.compression.CompressionUtils;

public abstract class AbstractGFFReader implements GFFReader {

	abstract public Iterator<GFFAlign> read(InputStream in) throws IOException;
	abstract public Iterator<GFFAlign> read(Reader in) throws IOException;

	public Iterator<GFFAlign> read(File in) throws IOException {
		try {
			return read(new FileInputStream(in));
		} catch(java.lang.Exception e) {
			throw new FileProcessingException(e,in);
		}
	}

	public Iterator<GFFAlign> read(URL address) throws IOException {
		try {
			return read(address.openStream());
		} catch(java.lang.Exception e) {
			throw new URLProcessingException(e,address);
		}
	}
	
	public Iterator<GFFAlign> read(String filename) throws IOException{
		return read(CompressionUtils.openFile(filename));
	}
	
	public Iterator<GFFAlign> readString(String in) throws IOException{
		return read(new StringReader(in));
	}

}
