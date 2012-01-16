package bfx.spectrum;

import java.io.PrintStream;

import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class SpectrumDumper extends Tool {

	@Parameter(names = {"--input","-i"}, description = "Input Spectrum Binary File",required=true)
	public String input;

	@Parameter(names = {"--output","-o"}, description = "Output Text File",required=false)
	public String output;
	
	@Override
	public void run() throws Exception {
		SpectrumBuilder spec = MemorySpectrumBuilder.load(input);
		PrintStream out = new PrintStream(getStdOut(output));
		spec.dump(out);
	}

	@Override
	public String getName() {
		return "specdump";
	}

}
