package bfx.exceptions;

public class ExceptionInSequenceProcessing extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private int positionInSequence;
	
	public ExceptionInSequenceProcessing(int positionInSequence,String message) {
		super(String.format("Error: '%s'\n\t in position %d of sequence.",message,positionInSequence));
		this.positionInSequence = positionInSequence;
	}

	public int getPositionInSequence() {
		return positionInSequence;
	}
}
