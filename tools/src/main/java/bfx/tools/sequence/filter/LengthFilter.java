package bfx.tools.sequence.filter;

public class LengthFilter extends FilterExpr {
	private int minLen;
	
	public LengthFilter(int minLen) {
		this.minLen=minLen;
	}
	
	@Override
	public boolean filter(int length, double meanQuality, String name) {
		return length>=minLen;
	}

}
