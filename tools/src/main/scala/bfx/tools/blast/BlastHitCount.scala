package bfx.tools.blast

import scala.collection._
import scala.collection.JavaConverters._
import com.beust.jcommander._
import bfx.tools.Tool
import com.weiglewilczek.slf4s.Logging
import bfx.blast.io._

case class BlastHitStat(var count:Int,val description:String,var scoreSum:Long);

class BlastHitCount extends Tool with Logging {
  @Parameter(names = Array("--input", "-i"), description = "Input file")
  val input = "-";

  @Parameter(names= Array("--min-length","-l"),description="Mininum HSP length")
  val minLen = 0;
  
  @Parameter(names= Array("--min-identity","-I"),description="Mininum HSP identity")
  val minIdentity = 0.0 

   
  def getName = "blastHitCount"
 
  def run {
    val parser = new BlastXMLReader(input)
    val stats = new mutable.HashMap[String,BlastHitStat]();

    logger.info("Running %s" format getName)
    
    for (r <- parser) {
      for (val hit <- r.getHits.asScala) {
        val maxHsp = hit.getHsps.asScala.reduceLeft((a, b) =>
          if (a.getScore > b.getScore) a else b)

        if (maxHsp.getAlignLen >= minLen && maxHsp.getIdentity >= minIdentity) {
          stats.get(hit.getAccession()) match {
            case Some(stat) => {
            	stat.count += 1
            	stat.scoreSum += maxHsp.getScore
            	stats.put(hit.getAccession, stat)
            }
            case None => {
             stats.put(hit.getAccession, new BlastHitStat(1,hit.getDef,maxHsp.getScore)) 
            }
          }
        }		
      }
    }
    logger.info("Founded %s hits".format(stats.size));
    
    for ((hit,s) <- stats) {
      
      println(Array(hit, s.count,
        s.scoreSum,
        s.description).mkString("\t"))
    }
  }
  
  def getGroup = "blast";
}

