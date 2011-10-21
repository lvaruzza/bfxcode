package biolite.io.impl;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

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
		Iterator<Sequence> it = reader.readString(">marafo\nACGT\nCGTT\n>2\nCAGCTAGCAT");
		Sequence seq1 = it.next();
		log.debug(seq1);
		Sequence seq2 = it.next();
		log.debug(seq2);
	}
}
