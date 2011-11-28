package bfx.exceptions;

import java.io.File;

public class MultipleFilesProcessingRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private File[] files;
	
	public MultipleFilesProcessingRuntimeException(java.lang.Exception e,File... files){
		super(e);
		this.files = files;
	}

	public MultipleFilesProcessingRuntimeException(java.lang.Exception e,String... filenames){
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
