package bfx.io.impl;
import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 import org.junit.Before;
import org.junit.Test;

import bfx.Sequence;
import bfx.io.SequenceReader;
import bfx.utils.io.TableReader;


public class TestLineBasedFastQIterator {
	private static Logger log = LoggerFactory.getLogger(TestLineBasedFastQIterator.class);
	
	SequenceReader reader;
	TableReader tblReader;
	
	@Before
	public void setUp() {
		reader = new FastQSequenceReader();
		tblReader = new TableReader();
	}

	@Test
	public void testReadFromStringOnlyOne() throws IOException {
		Iterator<Sequence> it = reader.readString(
				"@1\n" +
				"ACGT\n" +
				"+\n" +
				"5557");
		
		Sequence seq1 = it.next();
		log.debug(seq1.toString());
		assertEquals("ACGT",seq1.getSeqAsString());
		assertEquals("20 20 20 22",seq1.getQualAsString());
	}
	
	private void testReadFromFile(String seqFilename,String checkname) throws IOException, NoSuchAlgorithmException {
		//log.setLevel(Level.INFO);
		Iterator<Sequence> it = reader.read(seqFilename);
		Iterator<String[]> check = tblReader.read(checkname);
		while(it.hasNext()) {
			assertTrue(check.hasNext());
			String[] chk = check.next();
			Sequence seq = it.next();
			assertEquals(chk[0],seq.getId());
			
			String md5Seq = seq.digestSeq();
	        MessageDigest digest = MessageDigest.getInstance("md5");
	        digest.reset();
			String md5Qual = String.format("%032x",new BigInteger(1,digest.digest(seq.getQualAsString().getBytes())));
			log.debug(seq.getId());
						
			assertEquals(chk[1],md5Seq);
			assertEquals(chk[2],md5Qual);
		}
	}	
	
	@Test
	public void testReadIonFastQ() throws IOException, NoSuchAlgorithmException {
		testReadFromFile("data/test/ion.fastq.gz","data/test/ion.fastq.check.txt");
	}
}
