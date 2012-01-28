package bfx.tools.cli;

import bfx.process.ProgressMeter;

public class CLIProgressMeterFactory implements
		bfx.process.ProgressMeterFactory {

	@Override
	public ProgressMeter get() {
		ProgressMeter pc = new ProgressMeter();
		CLIProgressBar pb = new CLIProgressBar();
		pc.addObserver(pb);		
		
		return pc;
	}

}
