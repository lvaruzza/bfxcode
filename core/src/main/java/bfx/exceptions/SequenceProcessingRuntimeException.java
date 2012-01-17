package bfx.exceptions;

/**
 * 
 * Runtime Exception while processing a indexed sequence.
 * 
 * The error message will inform the error and the position in the sequence.
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 * 
 */
public class SequenceProcessingRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private long positionInSequence;
	
	public SequenceProcessingRuntimeException(long positionInSequence,String message) {
		super(String.format("Error: '%s'\n\t in position %d of sequence.",message,positionInSequence));
		this.positionInSequence = positionInSequence;
	}

	public long getPositionInSequence() {
		return positionInSequence;
	}
}
