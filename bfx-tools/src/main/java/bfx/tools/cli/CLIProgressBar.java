package bfx.tools.cli;

import java.util.Observable;
import java.util.Observer;

import bfx.ProgressCounter;

public class CLIProgressBar implements Observer {
	private double rate;
	
	public void showProgress(ProgressCounter pc) {
		double x = pc.getTicks();
		System.out.print('#');
		if (((long)x)%60==0)
			System.out.println(String.format(" count=%d ticks=%.0f rate=%.2e tick=%e",pc.getCount(),x,rate));
		System.out.flush();
	}

	@Override
	public void update(Observable o, Object arg) {		
		ProgressCounter pc=(ProgressCounter)o;
		long now = System.currentTimeMillis();
		rate = pc.getCount() / now; 
		pc.setTick((long)(10000.0/rate));
		showProgress(pc);
	}

}
