package bfx.tools.cli;

import java.util.Observable;
import java.util.Observer;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import bfx.ProgressCounter;

public class CLIProgressBar implements Observer {
	private long start = -1;
	private static PeriodFormatter periodFormatter = new PeriodFormatterBuilder()
															.printZeroNever()
															.appendDays()
															.appendSuffix(" day"," days")
															.appendSeparator(" ")
															.appendHours()
															.appendSuffix("h")
															.appendMinutes()
															.appendSuffix("m")
                                                            .printZeroAlways()															
															.appendSeconds()
															.appendSeparator(".")
															.appendMillis3Digit()
															.appendSuffix("s")
															.toFormatter();
															
	
	public void showProgress(ProgressCounter pc) {
		if (start==-1)
			start = System.currentTimeMillis();
		
		if (pc.isFinished()) {
			System.out.println();
			return;
		}
			
		long ticks = pc.getTicks();
		System.out.print('.');
		if (ticks%60==0) {
			long now = System.currentTimeMillis();
			long elapsed = now - start;
			Period period = new Period(elapsed);
			double rate  = (1000.0 * pc.getCount()) / elapsed;
			System.out.println(String.format(" [%,d recs in %s: %,.0f recs/s]",
					pc.getCount(), periodFormatter.print(period),rate));
			
			pc.setUpdateRate((long)(rate));
		}
		System.out.flush();
	}

	@Override
	public void update(Observable o, Object arg) {		
		ProgressCounter pc=(ProgressCounter)o;
		showProgress(pc);		
	}

}
