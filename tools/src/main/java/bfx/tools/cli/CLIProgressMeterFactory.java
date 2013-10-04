package bfx.tools.cli;

import bfx.process.ProgressMeter;

public class CLIProgressMeterFactory implements
		bfx.process.ProgressMeterFactory {

	private boolean enabled = true;
	
	@Override
	public void disable() {
		enabled = false;
	}
	
	@Override
	public void enable() {
		enabled = true;
	}
	
	@Override
	public ProgressMeter get() {
		ProgressMeter pc = new ProgressMeter();
		if (enabled) {
			CLIProgressBar pb = new CLIProgressBar();
			pc.addObserver(pb);		
		}
		return pc;
	}

}
