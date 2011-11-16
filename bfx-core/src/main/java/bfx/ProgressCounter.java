package bfx;

import java.util.Observable;

public  class ProgressCounter extends Observable {
	private double total = 100.0;
	private double count = 0;
	
	public void reset() {
		count = 0;
		this.setChanged();
		this.notifyObservers();
	}
	
	public void setTotal(double total) {
		this.total = 0;
		this.setChanged();
		this.notifyObservers();
	}
	
	public double getCount() {
		return count;
	}
	
	public void incr(double value) {
		count += value;
		this.setChanged();
		this.notifyObservers();
	}
	
	public double getPercent() {
		return count / total;
	}
}
