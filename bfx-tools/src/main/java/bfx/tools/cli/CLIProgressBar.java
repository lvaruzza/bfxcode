package bfx.tools.cli;

import java.util.Observable;
import java.util.Observer;

import bfx.ProgressCounter;

public class CLIProgressBar implements Observer {

	public void showProgress(ProgressCounter pc) {
		double x = pc.getTicks();
		System.out.print('#');
		if (((long)x)%60==0)
			System.out.println();
		System.out.println();
		System.out.flush();
	}

	@Override
	public void update(Observable o, Object arg) {
		showProgress((ProgressCounter)o);
	}

}
