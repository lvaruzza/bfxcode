package bfx.seqenc;

import java.util.Arrays;

import bfx.utils.TextUtils;

//A C G T
//A 0 1 2 3
//C 1 0 3 2
//G 2 3 0 1
//T 3 2 1 0


public class ColorSpaceTests {

	static byte[] idxs = new byte[] {'A','C','G','T'};
	static byte[][] cov = new byte[][] {{'0','1','2','3'},
		                                {'1','0','3','2'},
		                                {'2','3','0','1'},
		                                {'3','2','1','0'}};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for(byte base: idxs) {
			System.out.println(String.format("%c: %8s",base,Integer.toBinaryString(base)));
		}
		System.out.println(TextUtils.doubleLine());
		for(byte x: cov[0]) {
			System.out.println(String.format("%c: %8s",x,Integer.toBinaryString(x)));			
		}
		System.out.println(TextUtils.doubleLine());
		byte[] count = new byte[256];
		for(byte i=0;i<4;i++) {
			for(byte j=0;j<4;j++) {
				byte xor = (byte)((((('0'+i)&7) << 4)^(idxs[j])) );
				System.out.println(String.format("%c%c: %8s = %d",idxs[j],'0'+i,Integer.toBinaryString(xor),xor));
				count[xor]+=1;
			}
		}
		for(int i=0;i<256;i++) {
			if (count[i] != 0) 
				System.out.println(String.format("%8s: %d",Integer.toBinaryString(i),count[i]));
		}
	}

}
