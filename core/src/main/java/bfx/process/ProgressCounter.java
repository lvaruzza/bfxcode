package bfx.process;

import java.util.Observable;

/**
 * 
 * Observable to follow job progress
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 * 
 */
public  class ProgressCounter extends Observable {
	private long updateRate = 1;
	private long count = 0;
	private long count2 = 0;
	private long ticks = 0;

	private String name;
	
	private boolean finish = false;
	
	public void incr(long value) {
		count += value;
		count2 += value;
		if (count2 >=  updateRate) {
			count2 = 0;
			ticks ++;
			this.setChanged();
			this.notifyObservers();
		}
	}
	
	public void finish() {
		this.finish = true;
		this.setChanged();
		this.notifyObservers();		
	}
	
	public long getCount() {
		return count;
	}
	
	public long getTicks() {
		return ticks;
	}

	public boolean isFinished() {
		return finish;
	}
	
	public void reset() {
		count = 0;
		this.finish = false;
	}
	
	public void setUpdateRate(long updateRate) {
		this.updateRate = updateRate;
	}
	
	public void start(String name) {
		this.name = name;
		reset();
	}
	
	public String getProgressName() {
		return name;
	}
}
