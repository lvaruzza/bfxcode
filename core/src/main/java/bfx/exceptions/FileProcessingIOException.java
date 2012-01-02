package bfx.exceptions;

import java.io.File;
import java.io.IOException;

/**
 * 
 * IO Exception while processing a file.
 * 
 * The error message will inform the error and file name.
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 * 
 */
public class FileProcessingIOException extends IOException {
	private static final long serialVersionUID = 1L;
	private File file;
	
	public FileProcessingIOException(java.lang.Exception e,File f) {
		super(e);
		this.file = f;
	}

	@Override
	public String getMessage() {
		return String.format("Error in file '%s': %s",file.getAbsolutePath(),super.getMessage());
	}
}
