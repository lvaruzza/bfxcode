package biolite.io.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Level;
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
		Iterator<Sequence> it = reader.readString(">1\nACGT\nCGTT\n>2\nCCCCCC");
		Sequence seq1 = it.next();
		log.debug(seq1);		
		assertEquals("1",seq1.getId());
		assertEquals("ACGTCGTT",seq1.getSeqAsString());
		
		Sequence seq2 = it.next();
		assertEquals("2",seq2.getId());
		assertEquals("CCCCCC",seq2.getSeqAsString());
		log.debug(seq2);
	}
	
	@Test
	public void testReadFromFile() throws IOException {
		log.setLevel(Level.INFO);
		Iterator<Sequence> it = reader.read("data/test/ncbi_small.fasta");
		while(it.hasNext()) {
			log.debug(it.next().getId());
		}
	}	
}
