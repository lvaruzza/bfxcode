package biolite.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface AbstractWriter<T> {

	public void write(OutputStream output, T data) throws IOException;

	public void write(Writer writer, T data) throws IOException;

	public void write(File file, T data) throws IOException;

	public void write(String filename, T data) throws IOException;

}