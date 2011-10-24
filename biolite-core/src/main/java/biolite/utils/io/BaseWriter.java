package biolite.utils.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import biolite.exceptions.FileProcessingException;
import biolite.utils.compression.CompressionUtils;

/*
 * Implements the write method for several java potential reading
 * sources (Files, URL's, etc).
 * 
 */
public abstract class BaseWriter<T> implements AbstractWriter<T> {

	/* (non-Javadoc)
	 * @see biolite.utils.io.AbstractWriter#write(java.io.OutputStream, T)
	 */
	abstract public void write(OutputStream out,T data) throws IOException;
	/* (non-Javadoc)
	 * @see biolite.utils.io.AbstractWriter#write(java.io.Writer, T)
	 */
	abstract public void write(Writer writer,T data) throws IOException;

	/* (non-Javadoc)
	 * @see biolite.utils.io.AbstractWriter#write(java.io.File, T)
	 */
	public void write(File file,T data) throws IOException {
		try {
			write(CompressionUtils.openOutputStream(file),data);
		} catch(java.lang.Exception e) {
			throw new FileProcessingException(e,file);
		}
	}

	/* (non-Javadoc)
	 * @see biolite.utils.io.AbstractWriter#write(java.lang.String, T)
	 */
	public void write(String filename,T data) throws IOException{
		write(CompressionUtils.openOutputStream(filename),data);
	}
	
}
