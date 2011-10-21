package biolite.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public interface AbstractDualReader<T> {

	public T read(InputStream input1,InputStream input2) throws IOException;
	public T read(Reader reader1,Reader reader2) throws IOException;
	public T read(File file1,File file2) throws IOException;
	public T read(URL address1,URL address2) throws IOException;
	public T read(String filename1,String filename2) throws IOException;
	public T readString(String buffer1,String buffer2) throws IOException;

}