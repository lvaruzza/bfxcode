package bfx.exceptions;

import java.net.URL;

/**
 * 
 * Runtime Exception while processing multiple URL's at same time.
 * 
 * The error message will inform the error and URL's.
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 * 
 */
public class MultipleURLsProcessingRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private URL[] urls;
	
	public MultipleURLsProcessingRuntimeException(java.lang.Exception e,URL... urls) {
		super(e);
		this.urls = urls;
	}

	@Override
	public String getMessage() {
		StringBuilder ab = new StringBuilder();
		
		ab.append(String.format("Error %s in one of this URL's: ",super.getMessage()));
		for(URL u: urls) {
			ab.append('\t');
			ab.append(u);
			ab.append('\n');
		}
		return ab.toString();
	}
}
