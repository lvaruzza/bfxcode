package bfx.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface AbstractDualWriter<T> {
	public void write(OutputStream output1,OutputStream output2, T data) throws IOException;
	public void write(Writer writer1,Writer writer2, T data) throws IOException;
	public void write(File file1,File file2, T data) throws IOException;
	public void write(String filename1,String filename2, T data) throws IOException;
}