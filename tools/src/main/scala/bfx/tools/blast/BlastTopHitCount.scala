package bfx.tools.blast

import scala.collection.JavaConverters._
import scala.collection._
import bio.blast.model._
import bfx.blast.io._

class BlastHitCountMax {

}

object BlastHitCountMax {
  def main(args: Array[String]) {
    val parser = new BlastXMLReader(args(0))
    val idxCutoff = if (args.length > 0) args(1).toDouble else 0.0
    val counts = new mutable.HashMap[String, Int]()
    val descriptions = new mutable.HashMap[String, String]()
    val scoreSum = new mutable.HashMap[String, Int]()

    for (r <- parser) {
      if (r.getHits.size > 0) {
        val hit = r.getHits.asScala.head

        val maxHsp = hit.getHsps.asScala.reduceLeft((a, b) =>
          if (a.getScore > b.getScore) a else b)

        if (maxHsp.getIdentity > idxCutoff) {
          counts.get(hit.getAccession()) match {
            case Some(x) => counts.put(hit.getAccession, x + 1)
            case None => counts.put(hit.getAccession, 1)
          }
          scoreSum.get(hit.getAccession()) match {
            case Some(x) => scoreSum.put(hit.getAccession, x + maxHsp.getScore)
            case None => scoreSum.put(hit.getAccession, maxHsp.getScore)
          }
          descriptions.get(hit.getAccession) match {
            case None => descriptions.put(hit.getAccession, hit.getDef)
            case _ => None
          }
        }
      }
    }
    for (c <- counts) {
      println(Array(c._1, c._2,
        scoreSum.getOrElse(c._1, 0),
        descriptions.get(c._1).getOrElse("Nothing")).mkString("\t"))
    }
  }
}