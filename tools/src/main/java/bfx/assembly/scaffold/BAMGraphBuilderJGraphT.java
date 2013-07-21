package bfx.assembly.scaffold;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMSequenceRecord;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.utils.Pair;

public class BAMGraphBuilderJGraphT {
	private static Logger log = LoggerFactory.getLogger(BAMGraphBuilderJGraphT.class);
	private DescriptiveStatistics insertStats = new DescriptiveStatistics();
	private double inferedInsertMedian = 0;
	private double inferedInsertIQD = 0;
	private Map<Pair<String,String>,Long> edgeCount;
	
	public DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> buildGraph(String input,String output) throws Exception {
		SAMFileReader reader = new SAMFileReader(new File(input));
		long edges=0;
		edgeCount=new HashMap<Pair<String,String>,Long>();
		SAMFileHeader header = reader.getFileHeader();
		List<SAMSequenceRecord> seqs = header.getSequenceDictionary().getSequences();

		log.info("Reading BAM");
		for (SAMRecord aln:reader) {
			if(!aln.getDuplicateReadFlag() && !aln.getReadUnmappedFlag() && aln.getFirstOfPairFlag()) {
				//out.println(it);
				if (aln.getMateReferenceName().equals(aln.getReferenceName())) {
					//out.println(aln);
					calculateInsertSize(aln);
				} else {
					countEdge(aln);
					edges++;
				}
			}
		}
		reader.close();
		log.info("Finished Reading BAM");
		double q1 = insertStats.getPercentile(25);
		double q3 = insertStats.getPercentile(75);
		inferedInsertMedian = insertStats.getPercentile(50);
		inferedInsertIQD = q3-q1;
		log.info(String.format("insertStats: N=%,d median=%.1f IQD=%.1f",insertStats.getN(), 
				inferedInsertMedian,inferedInsertIQD));
		log.info(String.format("Edges: %,d",edges));

		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = buildGraph(edges,seqs);
		return graph;
	}

	private DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> buildGraph(long totalEdges,List<SAMSequenceRecord> seqs) {
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = 
				new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		for(SAMSequenceRecord seq: seqs) {
				graph.addVertex(seq.getSequenceName() + "+");
				graph.addVertex(seq.getSequenceName() + "-");
		}
		//graph.setAutoCreate( true );
		for (Entry<Pair<String,String>,Long> e: edgeCount.entrySet()) {
			if (e.getValue() >= 50) {
				DefaultWeightedEdge edge=graph.addEdge(e.getKey().fst,e.getKey().snd);
				graph.setEdgeWeight(edge, e.getValue());				
			}
		}
		return graph;
	}

	private String strand(boolean negative) {
		return negative ? "-" : "+";
	}
	
	private void countEdge(SAMRecord align) {
		String a=align.getReferenceName() + strand(align.getReadNegativeStrandFlag());
		String b=align.getMateReferenceName() + strand(align.getMateNegativeStrandFlag());
		
		Pair<String,String> pair;
		
		pair=new Pair<String,String>(a,b);
		
		if (edgeCount.containsKey(pair)) {
			edgeCount.put(pair,edgeCount.get(pair)+1);
		} else {
			edgeCount.put(pair,1l);
		}
	}

	private void calculateInsertSize(SAMRecord align) {
		insertStats.addValue(Math.abs(align.getInferredInsertSize()));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BAMGraphBuilderJGraphT gbuilder = new BAMGraphBuilderJGraphT();
		try {
			Graph<String,DefaultWeightedEdge> graph = gbuilder.buildGraph("data/mates.bam", null);
			//Viewer viewer = graph.display();
			for(String node: graph.vertexSet()) {
				System.out.println(node);
				for(DefaultWeightedEdge e: graph.edgesOf(node)) {
					System.out.println(String.format("\t%s %.0f",e.toString(),
							graph.getEdgeWeight(e)));
				}
			}
			//graph.display();
			//BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
			//GraphMLWriter<String,GraphEdge> writer= new GraphMLWriter<String,GraphEdge>();
			//writer.save(graph, out);
			//out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
