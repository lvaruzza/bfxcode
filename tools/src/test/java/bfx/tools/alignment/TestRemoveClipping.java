package bfx.tools.alignment;

import org.junit.Test;

import bfx.tools.cli.CLIProgressMeterFactory;

public class TestRemoveClipping {

	@Test
	public void test1() throws Exception {
		RemoveClipping rc = new RemoveClipping();
		rc.setProgressMeterFactory(new CLIProgressMeterFactory());
		rc.input = "data/test.bam";
		rc.output = "data/clipped.bam";
		rc.run();
	}
}
