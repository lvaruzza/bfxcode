package biolite.io.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import biojava.io.impl.FastaSequenceReader;
import biolite.Sequence;
import biolite.io.SequenceReader;
import biolite.utils.io.TableReader;

public class TestLineBasedFastaQualIterator {
	private static Logger log = Logger.getLogger(TestLineBasedFastaQualIterator.class);
	
	SequenceReader reader;
	TableReader tblReader;
	
	@Before
	public void setUp() {
		reader = new FastaSequenceReader();
		tblReader = new TableReader();
	}
	
	@Test
	public void testReadFromString() throws IOException {
		Iterator<Sequence> it = reader.readString(
				">1\n" +
				"ACGT\n" +
				"CGTT\n" +
				">2\n" +
				"CCCCCC\n",
				">1\n" +
				"0 1 2 3\n" +
				"4 5 6 7\n" +
				">2\n" +
				"9 9 9 9 9 9");
		
		Sequence seq1 = it.next();
		log.debug(seq1);		
		assertEquals("1",seq1.getId());
		assertEquals("ACGTCGTT",seq1.getSeqAsString());
		
		Sequence seq2 = it.next();
		assertEquals("2",seq2.getId());
		assertEquals("CCCCCC",seq2.getSeqAsString());
		log.debug(seq2);
	}
	
/*	
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
	public void testReadFromFileNCBISmall() throws IOException {
		testReadFromFile("data/test/ncbi_small.fasta","data/test/ncbi_small.check.txt");
	}

	@Test
	public void testReadFromCompressedFileNCBISmall() throws IOException {
		testReadFromFile("data/test/ncbi_small_compressed.fasta.gz","data/test/ncbi_small.check.txt");
	}

	@Test
	public void testReadCSFasta() throws IOException {
		testReadFromFile("data/test/sample.csfasta","data/test/sample.csfasta.check.txt");
	}
*/	
}
