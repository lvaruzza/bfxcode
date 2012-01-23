package bfx.spectrum;

import java.io.IOException;

import org.junit.Test;

import bfx.io.SequenceSource;

public class TestMapAndMegeSpectrumBuilder {

	@Test
	public void test0() throws IOException {
		MapAndMegeSpectrumBuilder sb = new MapAndMegeSpectrumBuilder(3,10);
		sb.add(SequenceSource.fromFile("fasta","data/test/spectrumU3.fasta"));		
	}
}
