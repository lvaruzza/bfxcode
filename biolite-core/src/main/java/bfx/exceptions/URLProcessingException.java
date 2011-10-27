package bfx.exceptions;

import java.io.IOException;
import java.net.URL;

public class URLProcessingException extends IOException {
	private static final long serialVersionUID = 1L;
	private URL url;
	
	public URLProcessingException(java.lang.Exception e,URL url) {
		super(e);
		this.url = url;
	}

	@Override
	public String getMessage() {
		return String.format("Error in file '%s': %s",url.toString(),super.getMessage());
	}
}
