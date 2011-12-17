package bfx.seqenc;

import org.apache.log4j.Logger;

import bfx.Sequence;


//   A C G T
// A 0 1 2 3
// C 1 0 3 2
// G 2 3 0 1
// T 3 2 1 0

public class Color {
	private static Logger log = Logger.getLogger(Color.class);
	
	private static byte[] enc = new byte[] {
		4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 0, 1, 2, 3, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 0, 4, 1, 4, 4, 4, 2, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 4, 1, 
        4, 4, 4, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};

	static byte[] bases = new byte[] {'A','C','G','T','N'};
	static byte[] colors = new byte[] {'0','1','2','3','.'};
	
/*	{{0,1,2,3,4},
   	 {1,0,3,2,4},
   	 {2,3,0,1,4},
   	 {3,2,1,0,4},
   	 {4,4,4,4,4}};
*/
	
	static byte[][] mat = new byte[][] 
							{{0,1,2,3,4},
				        	 {1,0,3,2,4},
				        	 {2,3,0,1,4},
				        	 {3,2,1,0,4},
				        	 {4,4,4,4,4}};
	
	public static byte naiveColorDecodeBase(byte a,byte b) {
		return bases[mat[enc[a]][enc[b]]];
	}

	public static byte naiveColorEncodeColor(byte a,byte b) {
		return colors[mat[enc[a]][enc[b]]];
	}

	public static byte[] naiveColorDecode(byte[] seq,int start,int length) {
		byte[] r = new byte[length];
		r[0]=seq[start];
		for(int i=start+1,j=1;i<start+length;i++,j++) {
			r[j] = naiveColorDecodeBase(r[j-1],seq[i]);
		}
		return r;
	}

	public static byte[] naiveColorEncode(byte[] seq,int start,int length) {
		byte[] r = new byte[length];
		r[0]=seq[start];
		for(int i=start+1,j=1;i<start+length;i++,j++) {
			//log.debug(String.format("i=%d j=%d",i,j));
			r[j] = naiveColorEncodeColor(seq[i-1],seq[i]);
		}
		return r;
	}
	
	public static byte[] colorDecode(byte[] seq) {
		return naiveColorDecode(seq,0,seq.length);
	}

	public static byte[] colorEncode(byte[] seq) {
		return naiveColorEncode(seq,0,seq.length);
	}
	
	public static Sequence colorDecode(Sequence seq) {
		byte[] s = seq.getSeq();
		return seq.changeSeq(colorDecode(s));
	}
	
	public static Sequence colorEncode(Sequence seq) {
		byte[] s = seq.getSeq();
		return seq.changeSeq(colorEncode(s));
	}
}	
