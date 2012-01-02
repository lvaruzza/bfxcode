package bfx.exceptions;


/**
 * 
 * Invalid format
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com> 
 * 
 */
public class InvalidFormatException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidFormatException(String message) {
		super(message);
	}

	public InvalidFormatException(String fmt,Object... args) {
		this(String.format(fmt,args));
	}
}
