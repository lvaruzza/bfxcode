package bfx;

/**
 * 
 * Mutable sequence builder
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 * 
 */
public interface SequenceBuilder {
	public void append(Sequence sequences);
	public Sequence getConstQual(String name,byte qual);
	public Sequence getWithQual(String name);
	public int getPosition();
}
