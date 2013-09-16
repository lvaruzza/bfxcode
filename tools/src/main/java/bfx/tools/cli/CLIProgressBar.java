package bfx.tools.cli;

import java.util.Observable;
import java.util.Observer;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import bfx.process.ProgressMeter;
import bfx.utils.TextUtils;

public class CLIProgressBar implements Observer {
	private static int lineSize = 60;
	
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
															
	
	private void printPerformance(ProgressMeter pc) {
		long now = System.currentTimeMillis();
		long elapsed = now - start;
		Period period = new Period(elapsed);
		double rate  = (1000.0 * pc.getCount()) / elapsed;
		System.out.println(String.format(" [%,d recs in %s: %,.0f recs/s]",
				pc.getCount(), periodFormatter.print(period),rate));
		
		pc.setUpdateRate((long)(rate));		
	}
	
	public void showProgress(ProgressMeter pc) {	
		long ticks = pc.getTicks();
		
		if (pc.isFinished()) {
			System.out.print('.');
			System.out.print(TextUtils.times(' ', (int)(lineSize-ticks%lineSize)-1));
			printPerformance(pc);
			System.out.println("Finished " + pc.getProgressName() +".\n");
			System.out.flush();
			start = -1;
			return;
		}
		
		if (start==-1) {
			start = System.currentTimeMillis();
			System.out.println("\nStarted " + pc.getProgressName() + ":");
			System.out.flush();
		}
		System.out.print('*');
		if (ticks%lineSize==0) {
			printPerformance(pc);
		}
		System.out.flush();
	}

	@Override
	public void update(Observable o, Object arg) {		
		ProgressMeter pc=(ProgressMeter)o;
		showProgress(pc);		
	}

}
