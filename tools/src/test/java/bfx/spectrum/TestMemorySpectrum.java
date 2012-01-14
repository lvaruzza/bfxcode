package bfx.spectrum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class TestMemorySpectrum {

	public MemorySpectrum createSpectrum() {
		MemorySpectrum spc = new MemorySpectrum(4);
		spc.add("ACGT".getBytes());
		spc.add("ACGT".getBytes());
		spc.add("CGTC".getBytes());
		spc.add("GTCA".getBytes());
		return spc;
	}
	
	@Test
	public void testDump() {
		MemorySpectrum spc = createSpectrum();
		
		spc.dump(System.out);
	}
	
	@Test
	public void testSaveLoad() throws IOException {
		try {
			MemorySpectrum spc = createSpectrum();
			ByteArrayOutputStream out = new ByteArrayOutputStream();		
			spc.save(out);
			byte[] data = out.toByteArray();
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			Spectrum spc2 = MemorySpectrum.load(in);
			spc2.dump(System.out);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}	
}
