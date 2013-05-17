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
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.Viewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.utils.Pair;

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
	
	public Graph buildGraph(String input,String output) throws Exception {
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
		log.info(String.format("insertStats: N=%,d median=%.1f IQD=%.1f",insertStats.getN(), 
				inferedInsertMedian,inferedInsertIQD));
		log.info(String.format("Edges: %,d",edges));

		Graph graph = buildGraph(edges,seqs);
		return graph;
	}

	private Graph buildGraph(long totalEdges,List<SAMSequenceRecord> seqs) {
		Graph graph = new MultiGraph("scaffold");
		long edgeId=0;
		for(SAMSequenceRecord seq: seqs) {
				Node node = graph.addNode(seq.getSequenceName());
				node.addAttribute("ui.label", seq.getSequenceName());
		}
		//graph.setAutoCreate( true );
		for (Entry<Pair<String,String>,Long> e: edgeCount.entrySet()) {
			if (e.getValue() >= 30) {
				/*System.out.println(String.format("<%s %s %d>",
					e.getKey().fst,
					e.getKey().snd,e.getValue()));*/
				String eid = Long.toString(edgeId++);
				Edge edge = graph.addEdge(eid,e.getKey().fst,e.getKey().snd,true);
				edge.addAttribute("count", e.getValue());
				
			}
		}
		return graph;
	}

	private void countEdge(SAMRecord align) {
		String a=align.getReferenceName();
		String b=align.getMateReferenceName();
		
		int flags=align.getFlags();
	
		Pair<String,String> pair;
		
		if (((flags & 0x10) == 1) && ((flags & 0x20) ==1)) {
			pair=new Pair<String,String>(b,a);
		} else {
			pair=new Pair<String,String>(a,b);
		}
		
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
			Graph graph = gbuilder.buildGraph("data/mates.bam", null);
			Viewer viewer = graph.display();

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
