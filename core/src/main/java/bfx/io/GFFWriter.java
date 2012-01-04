package bfx.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import bfx.GFF;
import bfx.utils.io.AbstractWriter;


/**
 * Write GFF records
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public interface GFFWriter extends AbstractWriter<Iterator<GFF>> {
	public void write(OutputStream out,GFF gff) throws IOException;
	public void write(File file,GFF gff) throws IOException;
	public void write(String filename,GFF gff) throws IOException;
}
