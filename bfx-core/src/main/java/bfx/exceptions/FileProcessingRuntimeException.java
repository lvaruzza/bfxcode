package bfx.exceptions;

import java.io.File;
import java.io.IOException;

public class FileProcessingRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private File file;
	
	public FileProcessingRuntimeException(java.lang.Exception e,File f) {
		super(e);
		this.file = f;
	}

	@Override
	public String getMessage() {
		return String.format("Error in file '%s': %s",file.getAbsolutePath(),super.getMessage());
	}
}
