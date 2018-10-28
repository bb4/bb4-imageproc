package com.barrybecker4.java2d.print

import java.awt._
import java.awt.geom.Rectangle2D
import java.awt.print.{PageFormat, Printable}
import java.io.{BufferedReader, File, FileReader}
import java.util
import javax.swing.JComponent
import scala.io.Source

/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
class FilePageRenderer(val file: File, val pageFormat: PageFormat)
  extends JComponent with Printable {

  private var mFontSize: Int = 12
  private val mFont: Font = new Font("Serif", Font.PLAIN, mFontSize)
  private val source = Source.fromFile(file)
  private val lines: Iterator[String] = source.getLines
  source.close()

  // Now paginate, based on the PageFormat.
  paginate(pageFormat)
  private var currentPage: Int = 0

  /**
    * Each element represents a single page. Each page's elements is
    * a Vector containing Strings that are the lines for a particular page.
    */
  private var pages: util.Vector[util.Vector[String]] = _


  def paginate(pageFormat: PageFormat): Unit = {
    currentPage = 0
    pages = new util.Vector[util.Vector[String]]
    var y: Float = mFontSize
    var page: util.Vector[String] = new util.Vector[String]

    for (line <- lines) {
      page.addElement(line)
      y += mFontSize
      if (y + mFontSize * 2 > pageFormat.getImageableHeight) {
        y = 0
        pages.addElement(page)
        page = new util.Vector[String]
      }
    }
    // Add the last page.
    if (page.size > 0) pages.addElement(page)
    // Set our preferred size based on the PageFormat.
    this.setPreferredSize(
      new Dimension(pageFormat.getImageableWidth.toInt, pageFormat.getImageableHeight.toInt))
    repaint()
  }

  override def paintComponent(g: Graphics): Unit = {
    val g2: Graphics2D = g.asInstanceOf[Graphics2D]
    // Make the background white.
    val r: Rectangle2D = new Rectangle2D.Float(0, 0, getPreferredSize.width, getPreferredSize.height)
    g2.setPaint(Color.white)
    g2.fill(r)
    // Get the current page.
    val page: util.Vector[_] = pages.elementAt(currentPage).asInstanceOf[util.Vector[_]]
    // Draw all the lines for this page.
    g2.setFont(mFont)
    g2.setPaint(Color.black)
    val x: Float = 0
    var y: Float = mFontSize
    var i: Int = 0
    while (i < page.size) {
      val line: String = page.elementAt(i).asInstanceOf[String]
      if (line.length > 0) g2.drawString(line, x.toInt, y.toInt)
      y += mFontSize
      i += 1
    }
  }

  override def print(g: Graphics, pageFormat: PageFormat, pageIndex: Int): Int = {
    if (pageIndex >= pages.size) return Printable.NO_SUCH_PAGE
    val savedPage: Int = currentPage
    currentPage = pageIndex
    val g2: Graphics2D = g.asInstanceOf[Graphics2D]
    g2.translate(pageFormat.getImageableX, pageFormat.getImageableY)
    paint(g2)
    currentPage = savedPage
    Printable.PAGE_EXISTS
  }

  def getCurrentPage: Int = currentPage
  def getNumPages: Int = pages.size

  def nextPage(): Unit = {
    if (currentPage < pages.size - 1) currentPage += 1
    repaint()
  }

  def previousPage(): Unit = {
    if (currentPage > 0) currentPage -= 1
    repaint()
  }
}