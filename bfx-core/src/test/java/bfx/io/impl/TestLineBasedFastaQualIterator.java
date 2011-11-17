package bfx.io.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import bfx.Sequence;
import bfx.io.SequenceReader;
import bfx.utils.io.TableReader;

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
	public void testReadFromStringOnlyOne() throws IOException {
		Iterator<Sequence> it = reader.readString(
				">1\n" +
				"ACGT",
				">1\n" +
				"1 2 3 4");
		
		Sequence seq1 = it.next();
		log.debug(seq1);
		assertEquals("ACGT",seq1.getSeqAsString());
		assertEquals("1 2 3 4",seq1.getQualAsString());
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
	
	
	private void testReadFromFile(String seqFilename,String qualFilename,String checkname) throws IOException, NoSuchAlgorithmException {
		//log.setLevel(Level.INFO);
		Iterator<Sequence> it = reader.read(seqFilename,qualFilename);
		Iterator<String[]> check = tblReader.read(checkname);
		while(it.hasNext()) {
			assertTrue(check.hasNext());
			String[] chk = check.next();
			Sequence seq = it.next();
			assertEquals(chk[0],seq.getId());
			
	        MessageDigest digest = MessageDigest.getInstance("md5");
	        digest.reset();
			String md5 = String.format("%032x",new BigInteger(1,digest.digest(seq.getQualAsString().getBytes())));
			log.debug(seq.getId());
			
			
			log.debug("=====> " + seq.getQualAsString());
			log.debug("=====> " + md5);
			log.debug("=====> " + chk[1]);
			assertEquals(chk[1],md5);
		}
	}	
	
	@Test
	public void testReadCSFasta() throws IOException, NoSuchAlgorithmException {
		testReadFromFile("data/test/sample.csfasta","data/test/sample.qual","data/test/sample.qual.check.txt");
	}

}
