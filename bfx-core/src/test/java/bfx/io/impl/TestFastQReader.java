package bfx.io.impl;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

import bfx.Sequence;
import bfx.io.SequenceReader;

public class TestFastQReader {
	@Test
	public void testBC016() throws IOException {
		SequenceReader sr = new FastQSequenceReader();
		Iterator<Sequence> it = sr.read("data/test/BC016.fq");

		while(it.hasNext()) {
			System.out.println(it.next());
		}
	}
}
