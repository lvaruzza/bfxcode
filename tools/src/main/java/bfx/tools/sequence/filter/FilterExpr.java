package bfx.tools.sequence.filter;

import bfx.Sequence;

public abstract class FilterExpr {
	public abstract boolean filter(int length,double meanQuality,String name);
	public boolean filter(Sequence seq) {
		return filter(seq.length(),seq.meanQuality(),seq.getId());
	}
	public boolean filter(int length,double meanQuality) {
		return filter(length,meanQuality,"");
	}
}
