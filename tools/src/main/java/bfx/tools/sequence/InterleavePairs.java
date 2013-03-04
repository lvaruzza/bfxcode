package bfx.tools.sequence;

import bfx.Sequence;
import bfx.io.SequenceSink;
import bfx.io.SequenceSource;
import bfx.process.ProgressMeter;
import bfx.sequencing.PairMatcher;
import bfx.sequencing.Platform;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class InterleavePairs extends Tool {

	@Parameter(names = {"--platform","--technology","-p"}, description = "Sequencing Platform (SOLiD, Ion Torrent, Illumina, etc)",required=true)
	public String platformName;

	@Parameter(names = {"--names","-R"}, description = "Regular expression for sequence names")
	public String namesRegexp;
	
	@Parameter(names = {"--left","--input1","-i1","-l"}, description = "Input File left",required=true)
	public String left;

	@Parameter(names = {"--right","--input2","-i2","-r"}, description = "Input File right",required=true)
	public String right;
	
	@Parameter(names = {"--leftQual","-q1","-lq"}, description = "Qual file left (only applicable for fasta format)")
	public String leftQual;

	@Parameter(names = {"--rightQual","-q2","-rq"}, description = "Qual file right (only applicable for fasta format)")
	public String rightQual;
	
	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;
	
	@Parameter(names = {"--output","--pairs","-o"}, description = "Output Interleaved Pairs File")
	public String pairs = "interleaved.fastq";

	
	@Parameter(names = {"--pairsQual","--outputQual","-pq","-oq"}, description = "Output Qual File (only appliable for fasta format)")
	public String pairsQual;
	
	@Parameter(names = {"-singlets","-s"}, description = "Output Singlets i, File")
	public String singlets = "singlets.fastq";

	@Parameter(names = {"--singletsQual","-sq"}, description = "Output Qaul File (only appliable for fasta format)")
	public String singletsQual;
	
	@Parameter(names = {"--outputFormat","-of"}, description = "Output Format")
	public String outputFormat;
	
	@Override
	public void run() throws Exception {
		Platform platform = Platform.get(platformName);
	
		final SequenceSource leftSeq = SequenceSource.fromFile(inputFormat,left,leftQual);
		final SequenceSource rightSeq = SequenceSource.fromFile(inputFormat,right,rightQual);

		final SequenceSink pairsOut = SequenceSink.fromFile(outputFormat, pairs, pairsQual);
		final SequenceSink singletsOut = SequenceSink.fromFile(outputFormat, singlets, singletsQual);
		
		ProgressMeter pm = getProgressMeterFactory().get();

		PairMatcher matcher = new PairMatcher(platform);
		matcher.setProgressMeter(pm);
		matcher.setLeft(leftSeq);
		matcher.setRight(rightSeq);
		pm.start("pairing");
		matcher.match(new PairMatcher.Callback() {
			@Override
			public void pair(Sequence left, Sequence right) throws Exception {
				pairsOut.write(left);
				pairsOut.write(right);
			}

			@Override
			public void singlet(Sequence singlet) throws Exception {
				singletsOut.write(singlet);
			}
			
		});
		pm.finish();
	}

	@Override
	public String getName() {
		return "interleave";
	}

	@Override
	public String getGroup() {
		return "sequence";
	}

}
