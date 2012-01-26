package bfx.tools.solid;

import java.io.File;

import bfx.io.SequenceSource;
import bfx.process.ProgressCounter;
import bfx.spectrum.MapAndMergeSpectrumBuilder;
import bfx.spectrum.SpectrumBuilder;
import bfx.tools.Report;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class ColorSpectrum extends Tool {
	//private static Logger log = Logger.getLogger(ColorSpectrum.class);
	
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

	@Parameter(names = "-mem", description = "Number of kmers in memory",required=false)
	public int kmersInMemory=10*1000*1000;

	@Parameter(names = {"--temp","-T"}, description = "Temporary files locations")
	public String temp=".";

	@Override
	public void run() throws Exception {
		SequenceSource seqs = SequenceSource.fromFile(format, input);
		File tempDir = new File(temp);
		SpectrumBuilder spectrum = new MapAndMergeSpectrumBuilder(k,kmersInMemory,tempDir);
		ProgressCounter pc = getProgressCounter();
		pc.start("Creating Spectrum");
		
		seqs.setProgressCounter(pc);
		spectrum.add(seqs);
		spectrum.finish();
		pc.finish();
		
		pc.reset();
		pc.start("Saving Spectrum");
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
