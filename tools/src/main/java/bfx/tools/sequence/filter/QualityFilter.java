package bfx.tools.sequence.filter;

public class QualityFilter extends FilterExpr {
	private double minQ;
	
	public QualityFilter(double minQual) {
		this.minQ=minQual;
	}
	
	@Override
	public boolean filter(int length, double meanQuality, String name) {
		return meanQuality>=minQ;
	}

}
