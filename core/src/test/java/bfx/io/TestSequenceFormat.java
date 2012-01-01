package bfx.io;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestSequenceFormat {

	@Test
	public void testGetReader() {
		SequenceReader reader = SequenceFormats.getReader("foo.fasta");
		assertEquals("bfx.io.impl.FastaSequenceReader",reader.getClass().getName());

		SequenceReader reader2 = SequenceFormats.getReader("foo.fastq");
		assertEquals("bfx.io.impl.FastQSequenceReader",reader2.getClass().getName());
	}
}
