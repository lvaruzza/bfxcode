package bfx;

import java.util.Observable;

public  class ProgressCounter extends Observable {
	private long tick = 1;
	private long count = 0;
	private boolean finish = false;
	
	public void reset() {
		count = 0;
		this.setChanged();
		this.notifyObservers();
		this.finish = false;
	}
	
	public void setTick(long tick) {
		this.tick = tick;
	}
	
	public double getCount() {
		return count;
	}
	
	public void incr(long value) {
		count += value;
		if (count % tick == 0) {
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
		return count * 1.0 / tick;
	}

	public boolean isFinished() {
		return finish;
	}
}
