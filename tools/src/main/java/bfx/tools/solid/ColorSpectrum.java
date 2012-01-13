package bfx.tools.solid;

import bfx.io.SequenceSource;
import bfx.spectrum.MemorySpectrum;
import bfx.spectrum.Spectrum;
import bfx.tools.Report;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class ColorSpectrum extends Tool {

	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--inputFormat","-if"}, description = "Input File Format",required=false)
	public String format;

	@Parameter(names = {"--output","-o"}, description = "Output Spectrum",required=true)
	public String output;
	
	@Parameter(names = {"--reportFormat","-rf"}, description = "Report Format",required=false)
	public String reportFormat;

	@Parameter(names = {"--reportOutput","-ro"}, description = "Report Output",required=false)
	public String reportOutput;
	
	@Parameter(names = "-k", description = "k value",required=true)
	public int k;
	
	@Override
	public void run() throws Exception {
		SequenceSource seqs = SequenceSource.fromFile(format, input);
		Spectrum spectrum = new MemorySpectrum(k);

		seqs.setProgressCounter(getProgressCounter());
		Iterable<byte[]> kmers = seqs.kmers(k,1,0);
		for(byte[] kmer: kmers) {
			spectrum.add(kmer);
		}
		pc.finish();
		spectrum.save(output);
		Report result = spectrum.getReport();
		result.write(getStdOut(reportOutput), reportFormat);
	}

	@Override
	public String getName() {
		return "spectrum";
	}

}
