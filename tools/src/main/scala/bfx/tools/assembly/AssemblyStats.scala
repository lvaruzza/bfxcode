package bfx.tools.assembly

import java.io.PrintWriter
import scala.collection._
import scala.collection.JavaConverters._
import scala.reflect.BeanInfo
import scala.reflect.BeanProperty
import com.beust.jcommander.Parameter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import bfx.io.SequenceSource
import bfx.tools.Report
import bfx.tools.Tool
import bfx.tools.cli.CLIToolRunner
import bfx.util.chart.Plotter
import org.jfree.data.xy.XYSeries
import org.jfree.data.statistics.HistogramDataset
import org.jfree.chart.ChartFactory
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.chart.plot.PlotOrientation
import java.io.File
import org.jfree.chart.annotations._
import org.jfree.ui.TextAnchor
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.AxisLocation
import org.jfree.chart.title.LegendTitle
import org.jfree.chart.LegendItemSource
import org.jfree.chart.LegendItemCollection
import org.jfree.chart.axis.LogarithmicAxis

@BeanInfo
class NStat (@BeanProperty val L:Int,
		     @BeanProperty val N:Int,
			 @BeanProperty val cumulative:Int)
@BeanInfo
class AssemblyStatReport(@BeanProperty val smallest: Int,
  @BeanProperty val largest: Int,
  @BeanProperty val ncontigs: Int,
  @BeanProperty val totalBases: Int,
  @BeanProperty val nstat: java.util.Map[Int, NStat]) extends Report {

  /*override def getMapper = {
	 val m = new ObjectMapper()
	 m.registerModule(DefaultScalaModule)
	 m.getSerializationConfig.without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
	 m		  
  }*/

  override def writeHuman(out: PrintWriter) {
    out.println(" Smallest Contig:\t%d" format smallest)
    out.println("  Largest Contig:\t%d" format largest)
    out.println("Number of contgs:\t%d" format ncontigs)
    out.println("     Total Bases:\t%d" format totalBases)
    out.println;
    out.println("Contig Size Statisitcs:")
    out.println("x\tNx\tLx")
    for ((cut, value) <- nstat.asScala) {
      out.println("%d\t%d\t%d".format(cut, value.N, value.L))
    }
  }
}

class AssemblyStats extends Tool {
  val plotter = new Plotter()

  def getName = "assemblystats"

  @Parameter(names = Array("--input", "-i"), description = "Input file")
  var input: String = "-"

  @Parameter(names = Array("--inputFormat", "-if"), description = "Input Format")
  var inputFormat = "fasta"

  @Parameter(names = Array("--output", "-o"), description = "Output File")
  var output: String = "-";

  //@Parameter(names = Array("--nstat"), description = "Cuts for N statistic. Default: N50, N90 and N95")
  var ncuts = List(50, 90, 95);

  @Parameter(names = Array("--outputFormat", "-of"), description = "Output Report Format")
  var outputFormat = "human";

  @Parameter(names = Array("--cumulativeGraph","-cg"), description = "Optional Cumulative Length Graph output file")
  var cumulativeLenGraph:String = null

  @Parameter(names = Array("--histogram","-h"), description = "Optional Contihg Length Histogram output file")
  var histogram:String = null
  
  
  def calculateNstats(sum: Int, psum: Array[Int], lengths: Array[Int]) = {
    val cuts = ncuts.sorted

    if (cuts.size > 0) {
      val nstats = new mutable.ListMap[Int, NStat]()
      var idx = 0; // cut index
      var i = 0
      for ((p, l) <- (psum zip lengths)) {
        if (idx < cuts.size && p * 100.0 / sum > cuts(idx)) {
          nstats(cuts(idx)) = new NStat(i,l,p)
          idx += 1
        }
        i += 1
      }
      nstats
    } else {
      new mutable.ListMap[Int, NStat]()
    }

  }

  def plotPsum(filename: String, psum: Array[Int],lengths:Array[Int],nstats:Map[Int,NStat]) {
    val serie1 = new XYSeries("Cumulative Length")
    val serie1p = new XYSeries("Cumulative Length(%)")
    var i = 1;
    val total = psum.last
    for (x <- psum) { 
      serie1.add(i, x);
      serie1p.add(i, x*100.0/total); 
      i += 1; 
    }
 
    val serie2 = new XYSeries("Length (%)")
    var j = 1;
    val larger = lengths.head
    for (x <- lengths) { serie2.add(j, x); j += 1; }

    
    val xyDataset = new XYSeriesCollection();
    xyDataset.addSeries(serie1p)
    //xyDataset.addSeries(serie2)
    
    val chart = ChartFactory.createScatterPlot(

      "Assembly Statistics", // Title
      "Contig Number", // X-Axis label
      "Cumulative Length (%)", // Y-Axis label
      xyDataset, // Dataset
      PlotOrientation.VERTICAL,
      true, // Show legend
      false,
      false)
    
    val axis2 = new NumberAxis("Cumulative Length");
    val plot = chart.getXYPlot
    
    plot.setRangeAxis(1, axis2);
    plot.setDataset(1, new XYSeriesCollection(serie1));
    plot.mapDatasetToRangeAxis(1, 1);
    plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
    chart.removeLegend
    
    val xyplot = chart.getXYPlot 
    for ((n,stat) <- nstats) {
    	  xyplot.addAnnotation(new XYLineAnnotation(stat.L,0,stat.L+1,n))
    	  val text = new XYTextAnnotation("N%d=%,d bp".format(n,stat.N),
    			  									stat.L+5,n/2.0)
    	  text.setTextAnchor(TextAnchor.BASELINE_LEFT)
    	  xyplot.addAnnotation(text)
    			  									
    	 
      }
    
    plotter.saveChartAsPDF(new File(filename), chart, 1000, 600)
  }

  def plotHistogram(filename: String,lengths:Array[Int],nstats:Map[Int,NStat]) {
    
    val hist = new HistogramDataset()
    val data = lengths.map ( _ * 1.0);
    //hist.addSeries("length", data, (Math.sqrt(data.length)).intValue)
    hist.addSeries("length", data, 100)

    
  val chart = ChartFactory.createHistogram(
            "Contigs Length Histogram", 
            null, 
            null, 
            hist, 
            PlotOrientation.VERTICAL, 
            true, 
            false, 
            false
        );
    chart.removeLegend
    
    val plot = chart.getXYPlot
    
    plot.setDomainAxis(0, new LogarithmicAxis("Contig Length"))
/*    val xyplot = chart.getXYPlot 
    for ((n,stat) <- nstats) {
    	  xyplot.addAnnotation(new XYLineAnnotation(stat.N,0,stat.N+1,n))
    	  val text = new XYTextAnnotation("N%d=%,d bp".format(n,stat.N),
    			  									stat.N+5,n/2.0)
    	  text.setTextAnchor(TextAnchor.BASELINE_LEFT)
    	  xyplot.addAnnotation(text)
    			  									
    	 
      }
*/
    
    plotter.saveChartAsPDF(new File(filename), chart, 1000, 600)
  }
  
  
  def plotPsumByLength(filename: String, psum: Array[Int],lengths:Array[Int]) {
    val serie = new XYSeries("Cumulative Length (bp)")
    for ((x,y) <- lengths zip psum) { serie.add(x,y); }
    val xyDataset = new XYSeriesCollection(serie);
    val chart = ChartFactory.createScatterPlot(

      "Cumulative Length", // Title
      "Contig Length", // X-Axis label
      "Cumulative Lenth", // Y-Axis label
      xyDataset, // Dataset
      PlotOrientation.VERTICAL,
      true, // Show legend
      false,
      false)
    plotter.saveChartAsPDF(new File(filename), chart, 1000, 600)
  }
  
  
  def run {
    val source = SequenceSource.fromFileOrStdin(inputFormat, input).asScala
    val lengths: Array[Int] = source.map(_.length).toArray.sortWith(
      (a, b) => (a compareTo b) > 0)

    // partial sum
    val psum = lengths.scan(0)(_ + _)
    val smallest = lengths.last
    val largest = lengths.head
    
    val sum = psum.last
    val nstats = calculateNstats(sum, psum, lengths)
    
    val report = new AssemblyStatReport(smallest,
      largest,
      lengths.length,
      sum,
      nstats.asJava)

    if(Option(cumulativeLenGraph).isDefined) {
      plotPsum(cumulativeLenGraph, psum,lengths,nstats)
    //plotPsumByLength("out2.pdf",psum,lengths)
    }
    if(Option(histogram).isDefined) {
      plotHistogram(histogram, lengths,nstats)
     }
    report.write(getStdOut(output), outputFormat);
  }
  
  override def getGroup = "assembly"
}

object AssemblyStats {
  def main(args: Array[String]) {
    CLIToolRunner.run(classOf[AssemblyStats], "-i data/assembly.fasta -h hist.pdf");
  }
}