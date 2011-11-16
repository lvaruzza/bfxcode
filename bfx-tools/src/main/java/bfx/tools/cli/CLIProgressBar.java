package bfx.tools.cli;

import java.util.Observable;
import java.util.Observer;

import bfx.ProgressCounter;

public class CLIProgressBar implements Observer {

	public void showProgress(ProgressCounter pc) {
		int x = (int)(pc.getTicks()/100.0*60);
		for(int i=0;i<x;i+=60 ) {
			for (int j=i;j<x;j++)
				System.out.print('#');
			System.out.println();
		}
		System.out.println();
		System.out.flush();
	}

	@Override
	public void update(Observable o, Object arg) {
		showProgress((ProgressCounter)o);
	}

}
