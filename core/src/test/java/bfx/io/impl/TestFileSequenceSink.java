package bfx.io.impl;

import org.junit.Test;

import bfx.io.SequenceSink;
import static org.junit.Assert.*;

/**
 * Test FileSequenceSink
 * @author varuzza
 *
 */
public class TestFileSequenceSink {

	@Test
	public void testConstructor() {
		SequenceSink sink1 = new FileSequenceSink("fasta","file1.fasta","file1.qual");
		SequenceSink sink2 = new FileSequenceSink("file1.fasta","file1.qual");
		assertNotNull(sink1);
		assertNotNull(sink2);
	}
}
