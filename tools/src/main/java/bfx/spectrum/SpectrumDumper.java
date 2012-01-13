package bfx.spectrum;

import java.io.PrintStream;

import bfx.tools.Tool;
import bfx.utils.Pair;

import com.beust.jcommander.Parameter;

public class SpectrumDumper extends Tool {

	@Parameter(names = {"--input","-i"}, description = "Input Spectrum Binary File",required=true)
	public String input;

	@Parameter(names = {"--output","-o"}, description = "Output Text File",required=false)
	public String output;
	
	@Override
	public void run() throws Exception {
		Spectrum spec = MemorySpectrum.load(input);
		PrintStream out = new PrintStream(getStdOut(output));
		for(Pair<byte[],Long> kmer:spec) {
			out.print(new String(kmer.fst));
			out.print("\t");
			out.print(kmer.snd);
		}
	}

	@Override
	public String getName() {
		return "specdump";
	}

}
