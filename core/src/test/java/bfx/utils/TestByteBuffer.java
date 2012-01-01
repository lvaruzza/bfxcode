package bfx.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestByteBuffer {

	@Test
	public void test1() {
		ByteBuffer bf = new ByteBuffer();
		
		bf.append("marafo".getBytes());
		bf.append("42".getBytes());
		System.out.println(new String(bf.get()));
		assertEquals("marafo42",new String(bf.get()));
	}
}
