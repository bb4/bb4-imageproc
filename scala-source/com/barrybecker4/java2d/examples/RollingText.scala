package com.barrybecker4.java2d.examples

import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._
import java.awt.font.GlyphVector
import java.awt.geom.AffineTransform


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object RollingText extends App {
  private val TEXT = "This an example of some pleasant rolling text..."
  private val FONT = new Font("Serif", Font.PLAIN, 24)

  val f = new RollingText.RollingTextFrame("RollingText")
  f.setSize(new Dimension(920, 450))
  f.setVisible(true)


  class RollingTextFrame(title: String) extends ApplicationFrame(title) {
    override def paint(g: Graphics): Unit = {
      super.paint(g)
      val g2 = g.asInstanceOf[Graphics2D]
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      val frc = g2.getFontRenderContext
      g2.translate(40, 80)
      val gv = FONT.createGlyphVector(frc, TEXT)
      drawText(g2, gv)
    }

    private def drawText(g2: Graphics2D, gv: GlyphVector): Unit = {
      val numLetters = gv.getNumGlyphs
      var i = 0
      while (i < numLetters) {
        val transformedGlyph = createTransformedGlyph(gv, numLetters, i)
        g2.fill(transformedGlyph)
        i += 1
      }
    }

    private def createTransformedGlyph(gv: GlyphVector, length: Int, i: Int) = {
      val p = gv.getGlyphPosition(i)
      val theta = i.toDouble / (length - 1).toDouble * Math.PI / 4
      val at = AffineTransform.getTranslateInstance(p.getX, p.getY)
      at.rotate(theta)
      val glyph = gv.getGlyphOutline(i)
      at.createTransformedShape(glyph)
    }
  }
}
