package bfx.tools.solid;

import org.apache.log4j.Logger;

import bfx.io.SequenceSource;
import bfx.spectrum.MemorySpectrumBuilder;
import bfx.spectrum.SpectrumBuilder;
import bfx.tools.Report;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class ColorSpectrum extends Tool {
	private static Logger log = Logger.getLogger(ColorSpectrum.class);
	
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
		SpectrumBuilder spectrum = new MemorySpectrumBuilder(k);

		seqs.setProgressCounter(getProgressCounter());
		Iterable<byte[]> kmers = seqs.kmers(k,1,0);
		for(byte[] kmer: kmers) {
			spectrum.add(kmer);
		}
		spectrum.finish();
		pc.finish();
		pc.reset();
		log.info("Saving Spectrum");
		spectrum.setProgressCounter(pc);		
		spectrum.save(output);
		pc.finish();
		Report result = spectrum.getReport();
		result.write(getStdOut(reportOutput), reportFormat);
	}

	@Override
	public String getName() {
		return "spectrum";
	}

}
