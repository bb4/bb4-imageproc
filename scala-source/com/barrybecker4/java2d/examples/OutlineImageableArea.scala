package com.barrybecker4.java2d.examples

import java.awt._
import java.awt.geom.Rectangle2D
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.Printable._
import java.awt.print.PrinterException
import java.awt.print.PrinterJob


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object OutlineImageableArea extends App {

  val pj = PrinterJob.getPrinterJob
  pj.setPrintable(new OutlineImageableArea.OutlinePrintable)
  if (pj.printDialog) try
    pj.print()
  catch {
    case e: PrinterException => println(e)
  }

  final private[examples] class OutlinePrintable extends Printable {
    override def print(g: Graphics, pf: PageFormat, pageIndex: Int): Int = {
      if (pageIndex != 0) return NO_SUCH_PAGE
      val g2 = g.asInstanceOf[Graphics2D]
      val outline =
        new Rectangle2D.Double(pf.getImageableX, pf.getImageableY, pf.getImageableWidth, pf.getImageableHeight)
      g2.setPaint(Color.black)
      g2.draw(outline)
      PAGE_EXISTS
    }
  }
}