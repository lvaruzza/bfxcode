package biolite.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public interface AbstractReader<T> {

	public T read(InputStream in) throws IOException;
	public T read(Reader in) throws IOException;
	public T read(File in) throws IOException;
	public T read(URL address) throws IOException;
	public T read(String filename) throws IOException;
	public T readString(String in) throws IOException;

}