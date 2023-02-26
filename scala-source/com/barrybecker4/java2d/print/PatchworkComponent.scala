package com.barrybecker4.java2d.print

import javax.swing._
import java.awt._
import java.awt.geom.Rectangle2D
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.Printable._


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
class PatchworkComponent(s: String) extends JComponent with Printable {
  private val mSide: Float = 36
  private val mOffset: Float = 36

  private val mColumns = 8
  private val mRows = 4
  private var mString = "Captivated"
  private val mFont = new Font("Serif", Font.PLAIN, 64)

  val xx: Float = mOffset
  val yy: Float = mOffset
  val halfSide: Float = mSide / 2
  var x0: Float = xx + halfSide
  var y0: Float = yy
  var x1: Float = xx + halfSide
  var y1: Float = yy + (mRows * mSide)
  private var mVerticalGradient = new GradientPaint(x0, y0, Color.darkGray, x1, y1, Color.lightGray, true)
  x0 = xx
  y0 = yy + halfSide
  x1 = xx + (mColumns * mSide)
  y1 = yy + halfSide
  private var mHorizontalGradient = new GradientPaint(x0, y0, Color.darkGray, x1, y1, Color.lightGray, true)

  def this() = {
    this("Patchwork")
  }

  override def paintComponent(g: Graphics): Unit = {
    val g2 = g.asInstanceOf[Graphics2D]
    g2.rotate(Math.PI / 24, mOffset, mOffset)
    var row = 0
    while (row < mRows) {
      var column = 0
      while (column < mColumns) {
        val x = column * mSide + mOffset
        val y = row * mSide + mOffset
        if (((column + row) % 2) == 0) g2.setPaint(mVerticalGradient)
        else g2.setPaint(mHorizontalGradient)
        val r = new Rectangle2D.Float(x, y, mSide, mSide)
        g2.fill(r)
        column += 1
      }
      row += 1
    }
    val frc = g2.getFontRenderContext
    val width = mFont.getStringBounds(mString, frc).getWidth.toFloat
    val lm = mFont.getLineMetrics(mString, frc)
    val x = ((mColumns * mSide) - width) / 2 + mOffset
    val y = ((mRows * mSide) + lm.getAscent) / 2 + mOffset
    g2.setFont(mFont)
    g2.setPaint(Color.white)
    g2.drawString(mString, x, y)
  }

  override def print(g: Graphics, pageFormat: PageFormat, pageIndex: Int): Int = {
    if (pageIndex != 0) return NO_SUCH_PAGE
    paintComponent(g)
    PAGE_EXISTS
  }
}