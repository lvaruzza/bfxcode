package bfx.exceptions;

import java.io.File;
import java.io.IOException;

/**
 * 
 * IO Exception while processing multiple files at same time.
 * 
 * The error message will inform the error and files names.
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 * 
 */
public class MultipleFilesProcessingIOException extends IOException {
	private static final long serialVersionUID = 1L;
	private File[] files;
	
	public MultipleFilesProcessingIOException(java.lang.Exception e,File... files){
		super(e);
		this.files = files;
	}

	public MultipleFilesProcessingIOException(java.lang.Exception e,String... filenames){
		super(e);
		files = new File[filenames.length];
		for(int i=0;i<filenames.length;i++)
			files[i]=new File(filenames[i]);
	}
	
	@Override
	public String getMessage() {
		StringBuilder ab = new StringBuilder();
		
		ab.append(String.format("Error %s in one of this files: ",super.getMessage()));
		for(File f: files) {
			ab.append('\t');
			ab.append(f.getAbsolutePath());
			ab.append('\n');
		}
		return ab.toString();
	}
}
