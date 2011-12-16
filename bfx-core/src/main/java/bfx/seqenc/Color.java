package bfx.seqenc;

import bfx.Sequence;


//   A C G T
// A 0 1 2 3
// C 1 0 3 2
// G 2 3 0 1
// T 3 2 1 0

public class Color {
	public static byte[] colorDecode(byte[] seq) {
		return null;
	}
	
	public static byte[] colorEncode(byte[] seq) {
		return null;
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
