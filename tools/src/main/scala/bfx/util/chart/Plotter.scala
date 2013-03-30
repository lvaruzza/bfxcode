package bfx.util.chart

import com.itextpdf.awt.FontMapper
import com.itextpdf.text.pdf.PdfWriter
import org.jfree.chart.JFreeChart
import java.io.BufferedOutputStream
import com.itextpdf.awt.PdfGraphics2D
import com.itextpdf.awt.DefaultFontMapper
import com.itextpdf.text.Rectangle
import java.io.OutputStream
import java.io.File
import com.itextpdf.text.Document
import java.io.FileOutputStream
import java.awt.geom.Rectangle2D
import grizzled.slf4j.Logging

class Plotter(val author:String,val subject:String,val fontMapper:FontMapper) extends Logging {
  //val defaultFontMapper = new DefaultFontMapper()
  
  def this() {
    this("unknown","chart",new DefaultFontMapper)
  }
  
  def writeChartAsPDF(out: OutputStream,
		  	chart: JFreeChart,
		  	width: Int, height: Int) {
    
    val pagesize = new Rectangle(width, height);
    val document = new Document(pagesize, 50, 50, 50, 50);
    val writer = PdfWriter.getInstance(document, out);
    document.addAuthor(author);
    document.addSubject(subject);
    document.open();
    val cb = writer.getDirectContent();
    val tp = cb.createTemplate(width, height);
    val g2 = new PdfGraphics2D(cb,width,height,fontMapper) 
    val r2D = new Rectangle2D.Double(0, 0, width, height);
    chart.draw(g2, r2D, null);
    g2.dispose();
    cb.addTemplate(tp, 0, 0);
    document.close();
  }

  def saveChartAsPDF(file: File,
    chart: JFreeChart,
    width: Int, height: Int) {
    val out = new BufferedOutputStream(new FileOutputStream(file));
    writeChartAsPDF(out, chart, width, height);
    out.close();
  }
  
  
}