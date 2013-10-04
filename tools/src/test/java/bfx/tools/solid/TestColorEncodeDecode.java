package bfx.tools.solid;

import org.junit.Before;
import org.junit.Test;

import bfx.process.ProgressMeterFactory;
import bfx.tools.cli.CLIProgressMeterFactory;

public class TestColorEncodeDecode {
	
	private ColorDecode cd;
	private ColorEncode ce;
	private ProgressMeterFactory pcf;
	
	@Before
	public void setup() {
		pcf = new CLIProgressMeterFactory();
	}
	
	@Test
	public void DecodeStdoutOutput() throws Exception {
		cd = new ColorDecode();
		cd.input = "../core/data/test/color.fastq";
		cd.setProgressMeterFactory(pcf);
		cd.run();
	}
	
	@Test
	public void EncodeStdoutOutput() throws Exception {
		ce = new ColorEncode();
		ce.input = "../core/data/test/ncbi_small.fasta";
		ce.setProgressMeterFactory(pcf);
		ce.run();
	}
	
}
