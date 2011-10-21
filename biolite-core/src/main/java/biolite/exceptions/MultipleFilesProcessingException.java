package biolite.exceptions;

import java.io.File;
import java.io.IOException;

public class MultipleFilesProcessingException extends IOException {
	private static final long serialVersionUID = 1L;
	private File[] files;
	
	public MultipleFilesProcessingException(java.lang.Exception e,File... files){
		super(e);
		this.files = files;
	}

	public MultipleFilesProcessingException(java.lang.Exception e,String... filenames){
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
