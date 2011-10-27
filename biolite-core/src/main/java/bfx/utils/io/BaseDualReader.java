package bfx.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import bfx.exceptions.MultipleFilesProcessingException;
import bfx.exceptions.MultipleURLsProcessingException;
import bfx.utils.compression.CompressionUtils;

/*
 * Implements the read method for several java potential reading
 * sources (Files, URL's, etc). This reads methos uses to sources 
 * read the data.
 * 
 */
public abstract class BaseDualReader<T> implements AbstractDualReader<T> {

	abstract public T read(InputStream input1,InputStream input2) throws IOException;
	abstract public T read(Reader reader1,Reader reader2) throws IOException;
	
	public T read(File file1,File file2) throws IOException {
		try {

			return read(CompressionUtils.openInputStream(file1),
					    CompressionUtils.openInputStream(file2));

		} catch(java.lang.Exception e) {
			throw new MultipleFilesProcessingException(e,file1,file2);
		}
	}
	public T read(URL address1,URL address2) throws IOException {
		try {
			return read(address1.openStream(),address2.openStream());
		} catch(java.lang.Exception e) {
			throw new MultipleURLsProcessingException(e,address1,address2);
		}
	}
	public T read(String filename1,String filename2) throws IOException{
		try {
			return read(CompressionUtils.openInputStream(filename1),
						CompressionUtils.openInputStream(filename2));
		} catch(java.lang.Exception e) {
			throw new MultipleFilesProcessingException(e,filename1,filename2);
		}
	}
	public T readString(String buff1,String buff2) throws IOException{
		return read(new StringReader(buff1),new StringReader(buff2));
	}
}
