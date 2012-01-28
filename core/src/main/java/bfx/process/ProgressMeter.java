package bfx.process;

import java.util.Observable;

/**
 * 
 * Observable to follow job progress
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 * 
 */
public  class ProgressMeter extends Observable {
	private long updateRate = 1;
	private long count = 0;
	private long count2 = 0;
	private long ticks = 0;
	private long startTime;
	private long elapsedTime;
	
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
		elapsedTime = System.currentTimeMillis() - startTime;
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
		
	public void setUpdateRate(long updateRate) {
		this.updateRate = updateRate;
	}
	
	public void start(String name) {
		this.name = name;
		startTime = System.currentTimeMillis();
	}
	
	public String getProgressName() {
		return name;
	}
	
	public long getElspsedTimeInMillis() {
		return elapsedTime;
	}
}
