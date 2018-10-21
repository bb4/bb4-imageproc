package com.barrybecker4.java2d.examples

import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._
import java.awt.geom.AffineTransform


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object TextRendering {
  def main(args: Array[String]): Unit = {
    val frame = new ApplicationFrame("TextRendering v1.0") {
      override def paint(g: Graphics): Unit = {
        super.paint(g)
        val g2 = g.asInstanceOf[Graphics2D]
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        // Transform the origin to the bottom center of the window.
        val d = getSize
        val ct = AffineTransform.getTranslateInstance(d.width / 2, d.height * 3 / 4)
        g2.transform(ct)
        // Get an appropriate font.
        val s = "jade"
        val f = new Font("Serif", Font.PLAIN, 128)
        g2.setFont(f)
        val limit = 6
        var i = 1
        while (i <= limit) { // Save the original transformation.
          val oldTransform = g2.getTransform
          val ratio = i.toFloat / limit.toFloat
          g2.transform(AffineTransform.getRotateInstance(Math.PI * (ratio - 1.0f)))
          val alpha = if (i == limit) 1.0f else ratio / 3
          g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha))
          g2.drawString(s, 0, 0)
          // Restore the original transformation.
          g2.setTransform(oldTransform)
          i += 1
        }
      }
    }
    frame.setVisible(true)
  }
}