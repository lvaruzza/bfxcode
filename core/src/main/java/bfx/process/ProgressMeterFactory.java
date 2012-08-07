package bfx.process;

public interface ProgressMeterFactory {
	public void disable();
	public void enable();
	public ProgressMeter get();
}
