package bfx.exceptions;


/**
 * 
 * Empty Iterator Exception
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 * 
 */
public class EmptyIteratorException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptyIteratorException() {
		super("Empty Iterator");
	}
}
