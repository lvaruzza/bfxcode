package bfx.exceptions;

import java.net.URL;

public class URLProcessingRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private URL url;
	
	public URLProcessingRuntimeException(java.lang.Exception e,URL url) {
		super(e);
		this.url = url;
	}

	@Override
	public String getMessage() {
		return String.format("Error in file '%s': %s",url.toString(),super.getMessage());
	}
}
