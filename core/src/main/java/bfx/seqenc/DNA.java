package bfx.seqenc;

public class DNA {
	public static double GC(byte[] s,int start,int end) {
		int gc=0;
		for(int i=start;i<end;i++) {
			if (s[i] == 'g' || s[i]=='c' || s[i]=='G' || s[i]=='C') {
				gc++;
			}		
		}
		double gcp=gc*1.0/(end-start);
		return gcp;
	}
}
