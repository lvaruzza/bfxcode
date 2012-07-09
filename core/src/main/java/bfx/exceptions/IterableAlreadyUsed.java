package bfx.exceptions;


/**
 * 
 * Empty Iterator Exception
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 * 
 */
public class IterableAlreadyUsed extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Exception Constructor.
	 * 
	 */
	public IterableAlreadyUsed() {
		super("You can't reuse this Iterable");
	}
}
