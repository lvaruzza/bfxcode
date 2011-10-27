package bfx.utils.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import bfx.exceptions.MultipleFilesProcessingException;
import bfx.utils.compression.CompressionUtils;

/*
 * Implements the write method for several java potential reading
 * sources (Files, URL's, etc).
 * 
 */
public abstract class BaseDualWriter<T> implements AbstractDualWriter<T> {	
	public abstract void write(OutputStream output1,OutputStream output2, T data) throws IOException;
	public abstract void write(Writer writer1,Writer writer2, T data) throws IOException;

	public void write(File file1,File file2, T data) throws IOException {
		try {
			write(CompressionUtils.openOutputStream(file1),
				  CompressionUtils.openOutputStream(file2),data);
		} catch(Exception e) {
			throw new MultipleFilesProcessingException(e,file1,file2);
		}
	}
	
	public void write(String filename1,String filename2, T data) throws IOException {
		try {
			write(CompressionUtils.openOutputStream(filename1),
				  CompressionUtils.openOutputStream(filename2),data);
		} catch(Exception e) {
			throw new MultipleFilesProcessingException(e,filename1,filename2);
		}
	}
}
