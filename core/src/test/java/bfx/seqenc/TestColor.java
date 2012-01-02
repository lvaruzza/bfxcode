package bfx.seqenc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bfx.Sequence;
import bfx.impl.SequenceQual;
import bfx.utils.TextUtils;

public class TestColor {

	@Test
	public void testHomopolimerEnc() {
		byte[] r = Color.colorEncode("AAAAA".getBytes());
		assertEquals("A0000",new String(r));
	}
	
	@Test
	public void testSeqEnc() {
		byte[] r = Color.colorEncode("ACGT".getBytes());
		assertEquals("A131",new String(r));
	}
	
	@Test
	public void testSeqDec() {
		byte[] r = Color.colorDecode("A131".getBytes());
		assertEquals("ACGT",new String(r));
	}

	@Test
	public void testSeqEnc2() {
		byte[] r = Color.colorEncode("TGCATCGCTGTCCAGCCCCTGGCGCGGGAAGATGGAGCGGTATGTTTGTCT".getBytes());
		assertEquals("T13132332112012300021033330020223102233013311001122",new String(r));
	}

	@Test
	public void testSeqDec2() {
		byte[] r = Color.colorDecode("T13132332112012300021033330020223102233013311001122".getBytes());
		assertEquals("TGCATCGCTGTCCAGCCCCTGGCGCGGGAAGATGGAGCGGTATGTTTGTCT",new String(r));
	}

	@Test
	public void testSeqEncDec2() {
		byte[] r = Color.colorDecode(
				Color.colorEncode("TGCATCGCTGTCCAGCCCCTGGCGCGGGAAGATGGAGCGGTATGTTTGTCT".getBytes()));
		assertEquals("TGCATCGCTGTCCAGCCCCTGGCGCGGGAAGATGGAGCGGTATGTTTGTCT",new String(r));
	}

	@Test
	public void testSeqEncWithN() {
		byte[] r = Color.colorEncode("TGCATCGCTGTCCAGCCCCTGGCGCNGGGAAGATGGAGCGGTATGTTTGTCT".getBytes());
		System.out.println(new String(r));
		//assertEquals("T13132332112012300021033330020223102233013311001122",new String(r));
	}
	
	@Test
	public void printDecMatrix() {
		for(byte b1: Color.bases) {
			for(byte b2: Color.bases) {			
				System.out.print(String.format("%c",Color.naiveColorEncodeColor(b1,b2)));
			}
			System.out.println();
		}
	}
	
	@Test
	public void testSeqDecode() {
		Sequence seq = new SequenceQual("a","A0123","1 2 3 4 5");
		System.out.println(seq);
		Sequence decoded = Color.colorDecode(seq);
		System.out.println(decoded);
	}
	
	@Test
	public void testSeqDecodeEncodeRandom() {
		ColorSeqGenerator gen = new ColorSeqGenerator(0.25,0.25,0.25,0.25,0.0);
		for(int i=0;i<10;i++) {
			Sequence seq = gen.genSeq("seq",(byte)'T', 60,(byte)0);
			System.out.println(seq);
			Sequence decoded = Color.colorDecode(seq);
			System.out.println(decoded);
			Sequence recoded = Color.colorEncode(decoded);
			System.out.println(recoded);
			assertEquals(seq,recoded);
			System.out.println(TextUtils.doubleLine());
		}
	}	

	@Test
	public void testSeqEncodeDecodeRandom() {
		DNASeqGenerator gen = new DNASeqGenerator(0.25,0.25,0.25,0.25,0.0);
		for(int i=0;i<10;i++) {
			Sequence seq = gen.genSeq("seq", 60,(byte)0);
			System.out.println(seq);
			Sequence encoded = Color.colorEncode(seq);
			System.out.println(encoded);
			Sequence decoded = Color.colorDecode(encoded);
			System.out.println(decoded);
			assertEquals(seq,decoded);
			System.out.println(TextUtils.doubleLine());
		}
	}	
	
}
