package bfx;

import java.util.Observable;

public  class ProgressCounter extends Observable {
	private long updateRate = 1;
	private long count = 0;
	private boolean finish = false;
	
	public void reset() {
		count = 0;
		this.setChanged();
		this.notifyObservers();
		this.finish = false;
	}
	
	public void setUpdateRate(long updateRate) {
		this.updateRate = updateRate;
	}
	
	public double getCount() {
		return count;
	}
	
	public void incr(long value) {
		count += value;
		if (count % updateRate == 0) {
			this.setChanged();
			this.notifyObservers();
		}
	}
	
	public void finish() {
		this.finish = true;
		this.setChanged();
		this.notifyObservers();		
	}
	
	public double getTicks() {
		return ((double)count) / updateRate;
	}

	public boolean isFinished() {
		return finish;
	}
}
