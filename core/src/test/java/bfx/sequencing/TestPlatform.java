package bfx.sequencing;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestPlatform {
	
	@Test
	public void testGetPlatform() {
		Platform solid = Platform.get("SOLiD");
		assertNotNull(solid);
	}
	
	@Test
	public void testSOLiDFragmentName() {
		Platform solid = Platform.get("SOLiD");
		assertEquals("469_26_42",solid.getFragmentName("469_26_42_F3"));
	}

	@Test
	public void testSOLiDCompare() {
		Platform solid = Platform.get("SOLiD");
		assertEquals(0,solid.compare("469_26_42_F3","469_26_42_F3"));
		assertEquals(-1,solid.compare("468_26_42_F3","469_26_42_F3"));
		assertEquals(1,solid.compare("469_26_42_F3","468_26_42_F3"));
		assertEquals(-1,solid.compare("469_25_42_F3","469_26_42_F3"));
		assertEquals(1,solid.compare("469_26_42_F3","469_25_42_F3"));
		assertEquals(-1,solid.compare("469_26_1_F3","469_26_100_F3"));
		assertEquals(1,solid.compare("469_26_100_F3","469_26_1_F3"));
	}
}
