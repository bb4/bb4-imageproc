package com.barrybecker4.java2d.examples

import com.barrybecker4.java2d.print.PatchworkComponent
import java.awt._
import java.awt.print.Book
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.PrinterException
import java.awt.print.PrinterJob
import java.awt.print.Printable.PAGE_EXISTS


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object Booker {
  def main(args: Array[String]): Unit = {
    val pj = PrinterJob.getPrinterJob
    // Create two Printables.
    val c1 = new PatchworkComponent("printable1")
    val c2 = new PatchworkComponent("printable2")
    c1.setSize(500, 400)
    c2.setSize(500, 400)
    val printable1 = new Booker.BookComponentPrintable(c1)
    val printable2 = new Booker.BookComponentPrintable(c2)
    // Create two PageFormats.
    val pageFormat1 = pj.defaultPage
    val pageFormat2 = pageFormat1.clone.asInstanceOf[PageFormat]
    pageFormat2.setOrientation(PageFormat.LANDSCAPE)
    // Create a Book.
    val book = new Book
    book.append(printable1, pageFormat1)
    book.append(printable2, pageFormat2)
    // Print the Book.
    pj.setPageable(book)
    if (pj.printDialog) try
      pj.print()
    catch {
      case e: PrinterException => println(e)
    }
  }

  final private[examples] class BookComponentPrintable(var mComponent: Component) extends Printable {
    override def print(g: Graphics, pageFormat: PageFormat, pageIndex: Int): Int = {
      val g2 = g.asInstanceOf[Graphics2D]
      g2.translate(pageFormat.getImageableX, pageFormat.getImageableY)
      mComponent.paint(g2)
      PAGE_EXISTS
    }
  }
}