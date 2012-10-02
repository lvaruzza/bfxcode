package bfx.tools.ncbi;

import java.util.List;

import bfx.Sequence;
import bfx.io.SequenceSink;
import bfx.io.SequenceSource;
import bfx.ncbi.Efetch;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class GetSeq extends Tool  {

	@Parameter(names = { "--ids", "-i" }, description = "List of sequence Ids", required = true,
			   variableArity = true)
	public List<String> ids;

	@Parameter(names = { "--db", "-d" }, description = "Database")
	public String dbname = "nucleotide";

	@Parameter(names = { "--output", "-o" }, description = "Output File")
	public String output;

	@Parameter(names = { "--outputQual", "-oq" }, description = "Output Qaul File (only appliable for fasta format)")
	public String outputQual;

	@Parameter(names = { "--outputFormat", "-of" }, description = "Output Format")
	public String outputFormat = "fasta";
	
	@Override
	public void run() throws Exception {
		SequenceSink sink = getSequenceSink(outputFormat, output, outputQual);
		
		Efetch efetch = new Efetch();
				
		SequenceSource src = efetch.nucleotide.getAll(ids.toArray(new String[0]));
		
		for(Sequence seq:src) {
			sink.write(seq);
		}
	}

	@Override
	public String getName() {
		return "getseq";
	}

	@Override
	public String getGroup() {
		return "ncbi";
	}
	
}
