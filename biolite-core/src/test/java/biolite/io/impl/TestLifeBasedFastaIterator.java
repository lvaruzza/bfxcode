package biolite.io.impl;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import biojava.io.impl.FastaSequenceReader;
import biolite.Sequence;
import biolite.io.SequenceReader;

public class TestLifeBasedFastaIterator {
	private static Logger log = Logger.getLogger(TestLifeBasedFastaIterator.class);
	
	SequenceReader reader;
	
	@Before
	public void setUp() {
		reader = new FastaSequenceReader();
	}
	
	@Test
	public void testReadFromString() throws IOException {
		Iterator<Sequence> it = reader.readString(">1\nACGT\nCGTT\n>2\nCCCCCC");
		Sequence seq1 = it.next();
		assertEquals("1",seq1.getId());
		assertEquals("ACGTCGTT",seq1.getSeqAsString());
		log.debug(seq1);
		Sequence seq2 = it.next();
		assertEquals("2",seq2.getId());
		assertEquals("CCCCCC",seq2.getSeqAsString());
		log.debug(seq2);
	}
}
