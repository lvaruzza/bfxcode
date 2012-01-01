package bfx.utils;

import org.junit.Test;

import bfx.seqenc.Color;

public class TestAliasMethod {
	
	@Test
	public void test0() {
		AliasMethodGenerator al = new AliasMethodGenerator(1.0/5,
										 0.9/5,
										 1.0/5,
										 2.0/5,
										 0.1/5);
		for(int i=0;i<120;i++) {
			System.out.print((char)Color.bases[al.next()]);
			if ((i+1)%60==0) System.out.println();
		}
		System.out.println();
	}
}
