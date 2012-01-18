package bfx.tools.sequence;

import java.io.File;
import java.io.FileOutputStream;

import bfx.GFF;
import bfx.Sequence;
import bfx.SequenceBuilder;
import bfx.impl.SequenceBuilderList;
import bfx.impl.SequenceConstQual;
import bfx.io.GFFWriter;
import bfx.io.SequenceSource;
import bfx.io.SequenceWriter;
import bfx.io.impl.FastaSequenceWriter;
import bfx.io.impl.OSGFFWriter;
import bfx.process.ProgressCounter;
import bfx.tools.Tool;
import bfx.utils.BFXMapUtils;
import bfx.utils.TextUtils;

import com.beust.jcommander.Parameter;

public class PseudoGenome extends Tool {

	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;
	
	@Parameter(names = {"--outputGenome","-o"}, description = "Output Genome")
	public String outputGenome;

	@Parameter(names = {"--outputGenome","-gff"}, description = "Output GFF Annotation")
	public String outputGFF;
	
	@Parameter(names = {"--genomeName","-n"}, description = "Genome Name")
	public String name;

	@Parameter(names = {"--annotationType","-type"}, description = "Annotation Type in GFF")
	public String annotationType = "CDS".intern();
	
	@Parameter(names = {"--spacerSize","-ss"}, description = "Number of N's to be put between each sequence")
	public int spacerSize = 100;
	
	@Parameter(names = {"--outputAnnotation","-a"}, description = "Output Annotation")
	public String annotation;
	
	@Override
	public void run() throws Exception {
		SequenceSource sequences = SequenceSource.fromFile(input,inputFormat);
		SequenceBuilder sb = new SequenceBuilderList();
		Sequence spacer = new SequenceConstQual("spacer",TextUtils.times('N',spacerSize),(byte)0);
		FileOutputStream gffout = new FileOutputStream(outputGFF);
		GFFWriter gffw = new OSGFFWriter();
		
		ProgressCounter pc = getProgressCounter();
		pc.start("Reading Sequences");
		sequences.setProgressCounter(pc);
		
		for(Sequence s: sequences) {
			int start = sb.getPosition();
			sb.append(s);
			int end = sb.getPosition();
			sb.append(spacer);
			GFF gff = new GFF(name,"pseudo-genome".intern(), annotationType, 
					start,end,0.0, '+', (byte)0, 
					BFXMapUtils.build("transcript_id",s.getId(),"gene_id",s.getId()));
			gffw.write(gffout, gff);
		}
		pc.finish();
		
		Sequence r = sb.getConstQual(name, (byte)0);
		SequenceWriter sw = new FastaSequenceWriter();
		sw.write(new File(outputGenome), r);
	}

	@Override
	public String getName() {
		return "pseudoGenome";
	}

}
