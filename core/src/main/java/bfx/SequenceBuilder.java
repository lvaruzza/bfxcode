package bfx;

public interface SequenceBuilder {
	public void append(Sequence sequences);
	public Sequence getConstQual(String name,byte qual);
	public Sequence getWithQual(String name);
	public int getPosition();
}
