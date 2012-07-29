package bfx.tools.blast

import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import bfx.utils.compression.CompressionUtils
import com.beust.jcommander.Parameter
import bfx.blast.io._

class BlastHitTable {
  @Parameter(names = Array("--input", "-i"), description = "Verbose output")
  val input = "-";

}

object BlastHitTable {
  def main(args:Array[String]) {
    
    val tool = new BlastHitTable()
    val jc = new JCommander(tool,args:_*);
    jc.setProgramName("blastHitTable");

    val parser = new BlastXMLReader(CompressionUtils.fileOrStdIn(tool.input))
    
    for (r <- parser) {
    	for (val hit <- r.getHits.asScala) {
    		println(Array(r.getQueryId,hit.getAccession,hit.getDef).mkString("\t"))
    	}
    }
  }
}