package bfx.ncbi;

import bfx.Sequence;

public class NCBIClient {
	public static void main(String... args) {
		Efetch efetch = new Efetch();
		Sequence seq = efetch.nucleotide.get("NM_004972");
		System.out.println(seq);
		
		Sequence seq2 = efetch.nucleotide.get("marafo");
	}
}
