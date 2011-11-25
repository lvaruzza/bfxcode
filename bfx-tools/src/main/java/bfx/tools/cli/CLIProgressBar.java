package bfx.tools.cli;

import java.util.Observable;
import java.util.Observer;

import bfx.ProgressCounter;

public class CLIProgressBar implements Observer {
	private long start = -1;
	
	public void showProgress(ProgressCounter pc) {
		if (start==-1)
			start = System.currentTimeMillis();
		
		if (pc.isFinished()) {
			System.out.println();
			return;
		}
			
		double x = pc.getTicks();
		System.out.print('#');
		if (((long)(x+1))%60==0) {
			long now = System.currentTimeMillis();
			long ticks  = (long) (10000.0 * (start - now) / pc.getCount());
			System.out.println(String.format(" count=%.0f ticks=%.0f newticks=%d",
					pc.getCount(),x,ticks));
			pc.setTick(ticks);
		}
		System.out.flush();
	}

	@Override
	public void update(Observable o, Object arg) {		
		ProgressCounter pc=(ProgressCounter)o;
		showProgress(pc);		
	}

}
