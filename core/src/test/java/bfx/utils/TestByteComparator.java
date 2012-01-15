package bfx.utils;

import java.util.Comparator;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestByteComparator {
	private static Comparator<byte[]> cmp = new ByteUtils.BytesComparator();
	
	private static void compare(String a,String b,int expected) {
		byte[] as = a.getBytes();
		byte[] bs = b.getBytes();
		int c = cmp.compare(as, bs);
		System.out.println(String.format("%s <=> %s:  %d",a,b,c));
		assertEquals(expected,c);
	}
	@Test
	public void test0() {
		compare("AAA","AAA",0);
		compare("AA","AAA",1);
		compare("AAA","AA",-1);
		compare("AAA","B",1);
		compare("B","AAA",-1);
		compare("AAA","AAB",1);
		compare("BBB","AAA",-1);
	}
}
