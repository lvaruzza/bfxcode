package bfx.io.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import bfx.Sequence;
import bfx.io.SequenceReader;
import bfx.utils.io.TableReader;

public class TestLineBasedFastaIterator {
	private static Logger log = Logger.getLogger(TestLineBasedFastaIterator.class);
	
	SequenceReader reader;
	TableReader tblReader;
	
	@Before
	public void setUp() {
		reader = new FastaSequenceReader();
		tblReader = new TableReader();
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
	
	private void testReadFromFile(String filename,String checkname) throws IOException {
		//log.setLevel(Level.INFO);
		Iterator<Sequence> it = reader.read(filename);
		Iterator<String[]> check = tblReader.read(checkname);
		while(it.hasNext()) {
			assertTrue(check.hasNext());
			String[] chk = check.next();
			Sequence seq = it.next();
			assertEquals(chk[0],seq.getId());
			assertEquals(chk[1],seq.getComments());
			String md5 = seq.digestSeq();
			assertEquals(chk[2],md5);
			log.debug(seq.getId());
			log.debug("=====> " + md5);
		}
	}	
	
	@Test
	public void testReadCSFasta() throws IOException {
		testReadFromFile("data/test/sample.csfasta","data/test/sample.csfasta.check.txt");
	}
}
