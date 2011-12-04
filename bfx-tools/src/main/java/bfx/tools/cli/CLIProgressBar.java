package bfx.tools.cli;

import java.util.Observable;
import java.util.Observer;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;

import bfx.ProgressCounter;

public class CLIProgressBar implements Observer {
	private long start = -1;
	private static PeriodFormatter periodFormatter = PeriodFormat.getDefault();
	
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
			long elapsed = now - start;
			Period period = new Period(elapsed);
			double rate  = (1000.0 * pc.getCount()) / elapsed;
			System.out.println(String.format(" %.0f records, elapsed=%s, rate=%.1f rec/s",
					pc.getCount(), periodFormatter.print(period),rate));
			
			pc.setUpdateRate((long)(10*rate));
		}
		System.out.flush();
	}

	@Override
	public void update(Observable o, Object arg) {		
		ProgressCounter pc=(ProgressCounter)o;
		showProgress(pc);		
	}

}
