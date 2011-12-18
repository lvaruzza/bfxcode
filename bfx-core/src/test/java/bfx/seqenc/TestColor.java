package bfx.seqenc;

import org.junit.Test;
import static org.junit.Assert.*;

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
}
