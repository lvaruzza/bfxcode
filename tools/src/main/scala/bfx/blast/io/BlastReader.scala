package bfx.blast.io

import javax.xml.stream._
import javax.xml.stream.events._
import java.io._
import javax.xml.bind._
import scala.collection.JavaConverters._
import bfx.blast.model._
import bfx.utils.compression.CompressionUtils

class BlastXMLReaderIterator(in:InputStream) extends Iterator[Result]{
  
  def createReader(in:InputStream) = {
      val xmlif = XMLInputFactory.newInstance()
      val xmler = xmlif.createXMLEventReader(in);
      val filter = new EventFilter() {
             def accept(event:XMLEvent) = event.isStartElement()
       };
       val xmlfer = xmlif.createFilteredReader(xmler, filter)
       //xmlfer
       xmler
  }
  
  val xmlfer = createReader(in)
  
  def nextIteration {
    while(xmlfer.hasNext)  {
      val ev = xmlfer.peek
      if (ev.isStartElement) {
	      val se = ev.asStartElement
	      val name:String = se.getName.getLocalPart
	      if (name == "Iteration") { 
	        return
	      }
      }
      xmlfer.next
    }
  } 
  
  val ctx = JAXBContext.newInstance("bfx.blast.model");
  val um = ctx.createUnmarshaller();
  nextIteration
  
  def hasNext = {
    if (!xmlfer.hasNext) false
    else {
      val ev = xmlfer.peek 
      if (ev.isStartElement && ev.asStartElement.getName.getLocalPart == "Iteration") true
      else {
        nextIteration
        xmlfer.hasNext
      }
    }
  }

  def next = {
    um.unmarshal(xmlfer) match {
      case r:Result => r
      case _ => throw new ClassCastException
    }    
  }
  
}

class BlastXMLReader(in:InputStream) extends Iterable[Result]{
  def this(file:String) {
	  this(CompressionUtils.openInputStream(file))
  }
  def iterator = new BlastXMLReaderIterator(in)
}

object BlastXMLReader {
  def main(args:Array[String]) {
    val parser = new BlastXMLReader("data/blastn.xml.gz")
    val it = parser.iterator
    val r = it.next
    println(r)
    for (hit <- r.getHits.asScala) {
      println(hit)
      println(hit.getHsps)
    }
    
    /*var count = 0;
    while(parser.hasNext) {
      val r = parser.next
      //println(r)
      count +=1
    }
    println(count)  */
  }
}