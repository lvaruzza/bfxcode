package bfx.tools.sequence.filter;

import bfx.Sequence;

public abstract class FilterExpr {
	public boolean filter(Sequence seq) {
		return filter(seq.length(),seq.meanQuality());
	}
	public abstract boolean filter(int length,double meanQuality);
}
