package bfx.assembly.scaffold;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMSequenceRecord;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.utils.Pair;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.io.GraphMLWriter;

public class BAMGraphBuilder {
	public static class GraphEdge {		
		public long id;
		public long count;

		public GraphEdge(long id, long count) {
			this.id=id;
			this.count=count;
		}
		@Override
		public String toString() {
			return "GraphEdge [id=" + id + ", count=" + count + "]";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (id ^ (id >>> 32));
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GraphEdge other = (GraphEdge) obj;
			if (id != other.id)
				return false;
			return true;
		}
		
		
	}
	
	private static Logger log = LoggerFactory.getLogger(BAMGraphBuilder.class);
	private DescriptiveStatistics insertStats = new DescriptiveStatistics();
	private double inferedInsertMedian = 0;
	private double inferedInsertIQD = 0;
	private Map<Pair<String,String>,Long> edgeCount;
	
	public Graph<String,GraphEdge> buildGraph(String input,String output) throws Exception {
		SAMFileReader reader = new SAMFileReader(new File(input));
		long edges=0;
		edgeCount=new HashMap<Pair<String,String>,Long>();
		SAMFileHeader header = reader.getFileHeader();
		List<SAMSequenceRecord> seqs = header.getSequenceDictionary().getSequences();

		log.info("Reading BAM");
		for (SAMRecord aln:reader) {
			if(!aln.getDuplicateReadFlag() && !aln.getReadUnmappedFlag()) {
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
		log.info(String.format("insertStats: %,d %.1f %.1f",insertStats.getN(), 
				inferedInsertMedian,inferedInsertIQD));
		log.info(String.format("Edges: %,d",edges));

		Graph<String,GraphEdge> graph = buildGraph(edges,seqs);
		return graph;
	}

	private Graph<String, GraphEdge> buildGraph(long totalEdges,List<SAMSequenceRecord> seqs) {
		Graph<String,GraphEdge> graph = new SparseMultigraph<String,GraphEdge>();
		long edgeId=0;
		for(SAMSequenceRecord seq: seqs) {
				graph.addVertex(seq.getSequenceName());
		}
		for (Entry<Pair<String,String>,Long> e: edgeCount.entrySet()) {
			if (e.getValue() >= 3) {
				System.out.println(String.format("<%s %s %d>",
					e.getKey().fst,
					e.getKey().snd,e.getValue()));
				graph.addEdge(new GraphEdge(edgeId++,e.getValue()), 
						e.getKey().fst,e.getKey().snd,EdgeType.DIRECTED);
			}
		}
		return graph;
	}

	private void countEdge(SAMRecord align) {
		Pair<String,String> pair= new Pair<String,String>(align.getReferenceName(),
									align.getMateReferenceName());
		
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
		BAMGraphBuilder gbuilder = new BAMGraphBuilder();
		try {
			Graph<String,GraphEdge> graph = gbuilder.buildGraph("data/mates.bam", null);
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
			GraphMLWriter<String,GraphEdge> writer= new GraphMLWriter<String,GraphEdge>();
			writer.save(graph, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
