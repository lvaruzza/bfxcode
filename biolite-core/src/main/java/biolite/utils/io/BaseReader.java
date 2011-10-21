package biolite.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import biolite.exceptions.FileProcessingException;
import biolite.exceptions.URLProcessingException;
import biolite.utils.compression.CompressionUtils;

/*
 * Implemens the read method for several java potencial reading
 * sources (Files, URL's, etc).
 * 
 */
public abstract class BaseReader<T> implements AbstractReader<T> {

	abstract public T read(InputStream in) throws IOException;
	abstract public T read(Reader in) throws IOException;

	public T read(File in) throws IOException {
		try {
			return read(new FileInputStream(in));
		} catch(java.lang.Exception e) {
			throw new FileProcessingException(e,in);
		}
	}

	public T read(URL address) throws IOException {
		try {
			return read(address.openStream());
		} catch(java.lang.Exception e) {
			throw new URLProcessingException(e,address);
		}
	}
	
	public T read(String filename) throws IOException{
		return read(CompressionUtils.openInputStream(filename));
	}
	
	public T readString(String in) throws IOException{
		return read(new StringReader(in));
	}

}
