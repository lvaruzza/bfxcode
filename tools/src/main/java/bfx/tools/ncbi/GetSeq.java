package bfx.tools.ncbi;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import bfx.Sequence;
import bfx.io.SequenceSink;
import bfx.io.SequenceSource;
import bfx.ncbi.Efetch;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class GetSeq extends Tool  {

	@Parameter(names = { "--ids", "-i" }, description = "List of sequence Ids", 
			   variableArity = true)
	public List<String> ids ;

	@Parameter(names = { "--idList", "-L" }, description = "File with a list of ids")
	public String file;
	
	@Parameter(names = { "--db", "-d" }, description = "Database")
	public String dbname = "nucleotide";

	@Parameter(names = { "--output", "-o" }, description = "Output File")
	public String output;

	@Parameter(names = { "--outputQual", "-oq" }, description = "Output Qual File (only appliable for fasta format)")
	public String outputQual;

	@Parameter(names = { "--outputFormat", "-of" }, description = "Output Format")
	public String outputFormat = "fasta";
	
	@Override
	public void run() throws Exception {
		SequenceSink sink = getSequenceSink(outputFormat, output, outputQual);
		
		if (ids == null) ids= new LinkedList<String>();
		if (file != null) {
			List<String> fid = FileUtils.readLines(new File(file));
			ids.addAll(fid);
		}
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
