package bfx.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import bfx.GFF;
import bfx.exceptions.FileProcessingIOException;
import bfx.utils.io.BaseWriter;


/**
 * Write GFF records
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public abstract class GFFWriter extends BaseWriter<Iterator<GFF>> {
	abstract public void write(OutputStream out,GFF gff) throws IOException;

	public void write(File file,GFF gff) throws IOException {
		try {
			write(new FileOutputStream(file),gff);
		} catch(IOException e) {
			throw new FileProcessingIOException(e,file);
		}		
	}
	
	public void write(String filename,GFF gff) throws IOException {
		write(new File(filename),gff);
	}
}
