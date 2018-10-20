package com.barrybecker4.java2d.examples

import java.awt._
import java.awt.geom.Rectangle2D
import java.awt.print.PageFormat
import java.awt.print.Paper
import java.awt.print.Printable
import java.awt.print.Printable._
import java.awt.print.PrinterException
import java.awt.print.PrinterJob


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object PageFormatMania extends App {

  val pj = PrinterJob.getPrinterJob
  val pf = pj.defaultPage
  val paper = new Paper
  val margin = 36 // half inch
  paper.setImageableArea(margin, margin, paper.getWidth - margin * 2, paper.getHeight - margin * 2)
  pf.setPaper(paper)
  pj.setPrintable(new PageFormatMania.ManiaPrintable, pf)
  if (pj.printDialog) try
    pj.print()
  catch {
    case e: PrinterException =>
      System.out.println(e)
  }

  private class ManiaPrintable extends Printable {
    override def print(g: Graphics, pf: PageFormat, pageIndex: Int): Int = {
      if (pageIndex != 0) return NO_SUCH_PAGE
      val g2 = g.asInstanceOf[Graphics2D]
      g2.setFont(new Font("Serif", Font.PLAIN, 36))
      g2.setPaint(Color.black)
      g2.drawString("ManiaPrintable", 100, 100)
      val outline = new Rectangle2D.Double(pf.getImageableX, pf.getImageableY, pf.getImageableWidth, pf.getImageableHeight)
      g2.draw(outline)
      PAGE_EXISTS
    }
  }
}