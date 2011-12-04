package bfx.utils.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import bfx.exceptions.FileProcessingIOException;
import bfx.exceptions.MultipleFilesProcessingIOException;
import bfx.utils.compression.CompressionUtils;

/*
 * Implements the write method for several java potential reading
 * sources (Files, URL's, etc).
 * 
 */
public abstract class BaseSingleAndDualWriter<T> implements AbstractWriter<T>,AbstractDualWriter<T> {

	/* (non-Javadoc)
	 * @see biolite.utils.io.AbstractWriter#write(java.io.OutputStream, T)
	 */
	abstract public void write(OutputStream out,T data) throws IOException;
	/* (non-Javadoc)
	 * @see biolite.utils.io.AbstractWriter#write(java.io.Writer, T)
	 */
	abstract public void write(Writer out,T data) throws IOException;

	/* (non-Javadoc)
	 * @see biolite.utils.io.AbstractWriter#write(java.io.File, T)
	 */
	public void write(File in,T data) throws IOException {
		try {
			write(new FileOutputStream(in),data);
		} catch(java.lang.Exception e) {
			throw new FileProcessingIOException(e,in);
		}
	}

	/* (non-Javadoc)
	 * @see biolite.utils.io.AbstractWriter#write(java.lang.String, T)
	 */
	public void write(String filename,T data) throws IOException{
		write(CompressionUtils.openOutputStream(filename),data);
	}

	
	public abstract void write(OutputStream output1,OutputStream output2, T data) throws IOException;
	public abstract void write(Writer writer1,Writer writer2, T data) throws IOException;

	public void write(File file1,File file2, T data) throws IOException {
		if(file2 == null) {
			write(file1,data);
			return;
		}
		try {

			write(CompressionUtils.openOutputStream(file1),
				  CompressionUtils.openOutputStream(file2),data);

		} catch(Exception e) {
			throw new MultipleFilesProcessingIOException(e,file1,file2);
		}
	}
	
	public void write(String filename1,String filename2, T data) throws IOException {
		if(filename2 == null) { 
			write(filename1,data);
			return;
		}
		try {
			write(CompressionUtils.openOutputStream(filename1),
				  CompressionUtils.openOutputStream(filename2),data);
		} catch(Exception e) {
			throw new MultipleFilesProcessingIOException(e,filename1,filename2);
		}
	}
}
