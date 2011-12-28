package bfx.seqenc;

import java.util.Arrays;

import bfx.utils.TextUtils;

//  A C G T
//A 0 1 2 3
//C 1 0 3 2
//G 2 3 0 1
//T 3 2 1 0

public class ColorSpaceProgs {

	static byte[] bases = new byte[] {'A','C','G','T','N'};
	static byte[][] c2b = new byte[][] {{'A','C','G','T','N'},
		                                {'C','A','T','G','N'},
		                                {'G','T','A','C','N'},
		                                {'T','G','C','A','N'},
		                                {'N','N','N','N','N'}};


	static byte[][] b2c = new byte[][] 
			{{0,1,2,3,4},
        	 {1,0,3,2,4},
        	 {2,3,0,1,4},
        	 {3,2,1,0,4},
        	 {4,4,4,4,4}};
	
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
	
	/**
	 * @param args
	 */
	public static void testXor(String[] args) {
		for(byte base: bases) {
			System.out.println(String.format("%c: %8s",base,Integer.toBinaryString(base)));
		}
		System.out.println(TextUtils.doubleLine());
		for(byte x: b2c[0]) {
			System.out.println(String.format("%c: %8s",x,Integer.toBinaryString(x)));			
		}
		System.out.println(TextUtils.doubleLine());
		byte[] count = new byte[256];
		for(byte i=0;i<4;i++) {
			for(byte j=0;j<4;j++) {
				byte xor = (byte)((((('0'+i)&7) << 4)^(bases[j])) );
				System.out.println(String.format("%c%c: %8s = %d",bases[j],'0'+i,Integer.toBinaryString(xor),xor));
				count[xor]+=1;
			}
		}
		for(int i=0;i<256;i++) {
			if (count[i] != 0) 
				System.out.println(String.format("%8s: %d",Integer.toBinaryString(i),count[i]));
		}
	}

	public static void printNaiveConvTbl(String[] args) {
		byte[] enc = new byte[255];
		Arrays.fill(enc, (byte)4);
		enc['a'] = 0;
		enc['c'] = 1;
		enc['g'] = 2;
		enc['t'] = 3;

		enc['A'] = 0;
		enc['C'] = 1;
		enc['G'] = 2;
		enc['T'] = 3;

		enc['0'] = 0;
		enc['1'] = 1;
		enc['2'] = 2;
		enc['3'] = 3;
		String pre = "private static byte[] enc = new byte[] {";
		System.out.print(pre);
		for(int i=0;i<254;i++) {
			System.out.print(String.format("%d, ",enc[i]));
			if ((i+1)%20 == 0)
				System.out.print("\n" + TextUtils.times(' ', pre.length()));
		}
		System.out.print(String.format("%d",enc[254]));
		System.out.println("};");
	}

	public static void printToBaseTbl(String[] args) {
		//byte a = 'A';
		//byte b = '1';
		
		for (byte a: bases) {
			byte[] r = new byte[5];
			for(byte bi=0;bi<5;bi++) {
 				byte ai = enc[a];
				//byte bi = enc[b];
				r[b2c[ai][bi]] = bi;				
				//System.out.print(String.format("%c ",bases[x]));
				System.out.println(String.format("%c %c %d",a,bases[b2c[ai][bi]],bi));
			}
			//Arrays.sort(r);
			System.out.println(String.format("%c %s",a,Arrays.toString(r)));
		}
		
		
	}
	
	public static void main(String[] args) {
		//testXor(args);
		//printNaiveConvTbl(args);
		printToBaseTbl(args);
	}
}
